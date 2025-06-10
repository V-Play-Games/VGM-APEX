/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.apex.core.player

import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.StreamKey
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.decoder.DecoderInputBuffer
import androidx.media3.exoplayer.FormatHolder
import androidx.media3.exoplayer.LoadingInfo
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.MediaPeriod
import androidx.media3.exoplayer.source.SampleStream
import androidx.media3.exoplayer.source.SampleStream.ReadDataResult
import androidx.media3.exoplayer.source.SampleStream.ReadFlags
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class ClippingMediaPeriod(
    val mediaPeriod: MediaPeriod, enableInitialDiscontinuity: Boolean,
    var startUs: Long,
    var endUs: Long
) : MediaPeriod, MediaPeriod.Callback {
    private var callback: MediaPeriod.Callback? = null
    private var sampleStreams: Array<ClippingSampleStream?>
    private var pendingInitialDiscontinuityPositionUs: Long
    private var clippingError: ClippingMediaSource.IllegalClippingException? = null

    init {
        sampleStreams = arrayOfNulls<ClippingSampleStream>(0)
        pendingInitialDiscontinuityPositionUs = if (enableInitialDiscontinuity) startUs else C.TIME_UNSET
    }

    fun updateClipping(startUs: Long, endUs: Long) {
        this.startUs = startUs
        this.endUs = endUs
    }

    fun setClippingError(clippingError: ClippingMediaSource.IllegalClippingException?) {
        this.clippingError = clippingError
    }

    override fun prepare(callback: MediaPeriod.Callback, positionUs: Long) {
        this.callback = callback
        mediaPeriod.prepare(this, positionUs)
    }

    @Throws(IOException::class)
    override fun maybeThrowPrepareError() {
        if (clippingError != null) {
            throw clippingError!!
        }
        mediaPeriod.maybeThrowPrepareError()
    }

    override fun getStreamKeys(trackSelections: MutableList<ExoTrackSelection>): MutableList<StreamKey> {
        return mediaPeriod.getStreamKeys(trackSelections)
    }

    override fun getTrackGroups(): TrackGroupArray {
        return mediaPeriod.getTrackGroups()
    }

    override fun selectTracks(
        selections: Array<ExoTrackSelection?>,
        mayRetainStreamFlags: BooleanArray,
        streams: Array<SampleStream?>,
        streamResetFlags: BooleanArray,
        positionUs: Long
    ): Long {
        sampleStreams = arrayOfNulls<ClippingSampleStream>(streams.size)
        val childStreams = arrayOfNulls<SampleStream>(streams.size)
        for (i in streams.indices) {
            sampleStreams[i] = streams[i] as ClippingSampleStream?
            childStreams[i] = if (sampleStreams[i] != null) sampleStreams[i]!!.childStream else null
        }
        val realEnablePositionUs =
            mediaPeriod.selectTracks(
                selections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs
            )
        val correctedEnablePositionUs: Long =
            enforceClippingRange(realEnablePositionUs,  /* minPositionUs= */positionUs, endUs)
        pendingInitialDiscontinuityPositionUs =
            if (this.isPendingInitialDiscontinuity
                && shouldKeepInitialDiscontinuity(realEnablePositionUs, positionUs, selections)
            )
                correctedEnablePositionUs
            else
                C.TIME_UNSET
        for (i in streams.indices) {
            if (childStreams[i] == null) {
                sampleStreams[i] = null
            } else if (sampleStreams[i] == null || sampleStreams[i]!!.childStream !== childStreams[i]) {
                sampleStreams[i] = this.ClippingSampleStream(childStreams[i]!!)
            }
            streams[i] = sampleStreams[i]
        }
        return correctedEnablePositionUs
    }

    override fun discardBuffer(positionUs: Long, toKeyframe: Boolean) {
        mediaPeriod.discardBuffer(positionUs, toKeyframe)
    }

    override fun reevaluateBuffer(positionUs: Long) {
        mediaPeriod.reevaluateBuffer(positionUs)
    }

    override fun readDiscontinuity(): Long {
        if (this.isPendingInitialDiscontinuity) {
            val initialDiscontinuityUs = pendingInitialDiscontinuityPositionUs
            pendingInitialDiscontinuityPositionUs = C.TIME_UNSET
            // Always read an initial discontinuity from the child, and use it if set.
            val childDiscontinuityUs = readDiscontinuity()
            return if (childDiscontinuityUs != C.TIME_UNSET) childDiscontinuityUs else initialDiscontinuityUs
        }
        val discontinuityUs = mediaPeriod.readDiscontinuity()
        if (discontinuityUs == C.TIME_UNSET) {
            return C.TIME_UNSET
        }
        return enforceClippingRange(discontinuityUs, startUs, endUs)
    }

    override fun getBufferedPositionUs(): Long {
        val bufferedPositionUs = mediaPeriod.getBufferedPositionUs()
        if (bufferedPositionUs == C.TIME_END_OF_SOURCE
            || (endUs != C.TIME_END_OF_SOURCE && bufferedPositionUs >= endUs)
        ) {
            return C.TIME_END_OF_SOURCE
        }
        return bufferedPositionUs
    }

    override fun seekToUs(positionUs: Long): Long {
        pendingInitialDiscontinuityPositionUs = C.TIME_UNSET
        for (sampleStream in sampleStreams) {
            if (sampleStream != null) {
                sampleStream.clearSentEos()
            }
        }
        return enforceClippingRange(mediaPeriod.seekToUs(positionUs), startUs, endUs)
    }

    override fun getAdjustedSeekPositionUs(positionUs: Long, seekParameters: SeekParameters): Long {
        if (positionUs == startUs) {
            // Never adjust seeks to the start of the clipped view.
            return startUs
        }
        val clippedSeekParameters = clipSeekParameters(positionUs, seekParameters)
        return mediaPeriod.getAdjustedSeekPositionUs(positionUs, clippedSeekParameters)
    }

    override fun getNextLoadPositionUs(): Long {
        val nextLoadPositionUs = mediaPeriod.getNextLoadPositionUs()
        if (nextLoadPositionUs == C.TIME_END_OF_SOURCE
            || (endUs != C.TIME_END_OF_SOURCE && nextLoadPositionUs >= endUs)
        ) {
            return C.TIME_END_OF_SOURCE
        }
        return nextLoadPositionUs
    }

    override fun continueLoading(loadingInfo: LoadingInfo): Boolean {
        return mediaPeriod.continueLoading(loadingInfo)
    }

    override fun isLoading(): Boolean {
        return mediaPeriod.isLoading()
    }

    // MediaPeriod.Callback implementation.
    override fun onPrepared(mediaPeriod: MediaPeriod) {
        if (clippingError != null) {
            return
        }
        Assertions.checkNotNull(callback).onPrepared(this)
    }

    override fun onContinueLoadingRequested(source: MediaPeriod) {
        Assertions.checkNotNull(callback).onContinueLoadingRequested(this)
    }

    val isPendingInitialDiscontinuity: Boolean
        /* package */
        get() = pendingInitialDiscontinuityPositionUs != C.TIME_UNSET

    private fun clipSeekParameters(positionUs: Long, seekParameters: SeekParameters): SeekParameters {
        val toleranceBeforeUs =
            Util.constrainValue(
                seekParameters.toleranceBeforeUs,  /* min= */0,  /* max= */positionUs - startUs
            )
        val toleranceAfterUs =
            Util.constrainValue(
                seekParameters.toleranceAfterUs,  /* min= */
                0,  /* max= */
                if (endUs == C.TIME_END_OF_SOURCE) Long.Companion.MAX_VALUE else endUs - positionUs
            )
        if (toleranceBeforeUs == seekParameters.toleranceBeforeUs
            && toleranceAfterUs == seekParameters.toleranceAfterUs
        ) {
            return seekParameters
        } else {
            return SeekParameters(toleranceBeforeUs, toleranceAfterUs)
        }
    }

    /** Wraps a [SampleStream] and clips its samples.  */
    private inner class ClippingSampleStream(val childStream: SampleStream) : SampleStream {
        private var sentEos = false

        fun clearSentEos() {
            sentEos = false
        }

        override fun isReady(): Boolean {
            return !isPendingInitialDiscontinuity && childStream.isReady()
        }

        @Throws(IOException::class)
        override fun maybeThrowError() {
            childStream.maybeThrowError()
        }

        override fun readData(
            formatHolder: FormatHolder, buffer: DecoderInputBuffer, readFlags: @ReadFlags Int
        ): Int {
            if (isPendingInitialDiscontinuity) {
                return C.RESULT_NOTHING_READ
            }
            if (sentEos) {
                buffer.setFlags(C.BUFFER_FLAG_END_OF_STREAM)
                return C.RESULT_BUFFER_READ
            }
            val bufferedPositionUs = getBufferedPositionUs()
            val result: @ReadDataResult Int = childStream.readData(formatHolder, buffer, readFlags)
            if (result == C.RESULT_FORMAT_READ) {
                val format = Assertions.checkNotNull<Format>(formatHolder.format)
                if (format.encoderDelay != 0 || format.encoderPadding != 0) {
                    // Clear gapless playback metadata if the start/end points don't match the media.
                    val encoderDelay = if (startUs != 0L) 0 else format.encoderDelay
                    val encoderPadding = if (endUs != C.TIME_END_OF_SOURCE) 0 else format.encoderPadding
                    formatHolder.format =
                        format
                            .buildUpon()
                            .setEncoderDelay(encoderDelay)
                            .setEncoderPadding(encoderPadding)
                            .build()
                }
                return C.RESULT_FORMAT_READ
            }
            if (endUs != C.TIME_END_OF_SOURCE
                && ((result == C.RESULT_BUFFER_READ && buffer.timeUs >= endUs)
                        || (result == C.RESULT_NOTHING_READ && bufferedPositionUs == C.TIME_END_OF_SOURCE && !buffer.waitingForKeys))
            ) {
                buffer.clear()
                buffer.setFlags(C.BUFFER_FLAG_END_OF_STREAM)
                sentEos = true
                return C.RESULT_BUFFER_READ
            }
            return result
        }

        override fun skipData(positionUs: Long): Int {
            if (isPendingInitialDiscontinuity) {
                return C.RESULT_NOTHING_READ
            }
            return childStream.skipData(positionUs)
        }
    }

    companion object {
        private fun shouldKeepInitialDiscontinuity(
            startUs: Long, requestedPositionUs: Long, selections: Array<ExoTrackSelection?>
        ): Boolean {
            // If the source adjusted the start position to be before the requested position, we need to
            // report a discontinuity to ensure renderers decode-only the samples before the requested start
            // position.
            if (startUs < requestedPositionUs) {
                return true
            }
            // If the clipping start position is non-zero, the clipping sample streams will adjust
            // timestamps on buffers they read from the unclipped sample streams. These adjusted buffer
            // timestamps can be negative, because sample streams provide buffers starting at a key-frame,
            // which may be before the clipping start point. When the renderer reads a buffer with a
            // negative timestamp, its offset timestamp can jump backwards compared to the last timestamp
            // read in the previous period. Renderer implementations may not allow this, so we signal a
            // discontinuity which resets the renderers before they read the clipping sample stream.
            // However, for tracks where all samples are sync samples, we assume they have random access
            // seek behaviour and do not need an initial discontinuity to reset the renderer.
            if (startUs != 0L) {
                for (trackSelection in selections) {
                    if (trackSelection != null) {
                        val selectedFormat = trackSelection.getSelectedFormat()
                        if (!MimeTypes.allSamplesAreSyncSamples(
                                selectedFormat.sampleMimeType, selectedFormat.codecs
                            )
                        ) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        private fun enforceClippingRange(
            positionUs: Long, minPositionUs: Long, maxPositionUs: Long
        ): Long {
            var positionUs = positionUs
            positionUs = max(positionUs, minPositionUs)
            if (maxPositionUs != C.TIME_END_OF_SOURCE) {
                positionUs = min(positionUs, maxPositionUs)
            }
            return positionUs
        }
    }
}
