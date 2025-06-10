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

import androidx.annotation.IntDef
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.source.ForwardingTimeline
import androidx.media3.exoplayer.source.MediaPeriod
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MediaSource.MediaPeriodId
import androidx.media3.exoplayer.source.WrappingMediaSource
import androidx.media3.exoplayer.upstream.Allocator
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class ClippingMediaSource private constructor(builder: Builder) : WrappingMediaSource(builder.mediaSource) {
    class Builder(mediaSource: MediaSource?) {
        internal val mediaSource: MediaSource

        internal var startPositionUs: Long = 0
        internal var endPositionUs: Long
        internal var enableInitialDiscontinuity = true
        internal var allowDynamicClippingUpdates = false
        internal var relativeToDefaultPosition = false
        internal var allowUnseekableMedia = false
        internal var buildCalled = false

        init {
            this.mediaSource = Assertions.checkNotNull<MediaSource>(mediaSource)
            this.endPositionUs = C.TIME_END_OF_SOURCE
        }

        fun setStartPositionMs(startPositionMs: Long): Builder {
            return setStartPositionUs(Util.msToUs(startPositionMs))
        }

        fun setStartPositionUs(startPositionUs: Long): Builder {
            Assertions.checkArgument(startPositionUs >= 0)
            Assertions.checkState(!buildCalled)
            this.startPositionUs = startPositionUs
            return this
        }

        fun setEndPositionMs(endPositionMs: Long): Builder {
            return setEndPositionUs(Util.msToUs(endPositionMs))
        }

        fun setEndPositionUs(endPositionUs: Long): Builder {
            Assertions.checkState(!buildCalled)
            this.endPositionUs = endPositionUs
            return this
        }

        fun setEnableInitialDiscontinuity(enableInitialDiscontinuity: Boolean): Builder {
            Assertions.checkState(!buildCalled)
            this.enableInitialDiscontinuity = enableInitialDiscontinuity
            return this
        }

        fun setAllowDynamicClippingUpdates(allowDynamicClippingUpdates: Boolean): Builder {
            Assertions.checkState(!buildCalled)
            this.allowDynamicClippingUpdates = allowDynamicClippingUpdates
            return this
        }

        fun setRelativeToDefaultPosition(relativeToDefaultPosition: Boolean): Builder {
            Assertions.checkState(!buildCalled)
            this.relativeToDefaultPosition = relativeToDefaultPosition
            return this
        }

        fun setAllowUnseekableMedia(allowUnseekableMedia: Boolean): Builder {
            Assertions.checkState(!buildCalled)
            this.allowUnseekableMedia = allowUnseekableMedia
            return this
        }

        fun build(): ClippingMediaSource {
            buildCalled = true
            return ClippingMediaSource(this)
        }
    }

    class IllegalClippingException
    @JvmOverloads constructor(
        val reason: @Reason Int, startUs: Long = C.TIME_UNSET, endUs: Long = C.TIME_UNSET
    ) : IOException(
        "Illegal clipping: " + getReasonDescription(
            reason, startUs, endUs
        )
    ) {
        @MustBeDocumented
        @Retention(AnnotationRetention.SOURCE)
        @Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER)
        @IntDef(REASON_INVALID_PERIOD_COUNT, REASON_NOT_SEEKABLE_TO_START, REASON_START_EXCEEDS_END)
        annotation class Reason

        companion object {
            const val REASON_INVALID_PERIOD_COUNT: Int = 0

            const val REASON_NOT_SEEKABLE_TO_START: Int = 1

            const val REASON_START_EXCEEDS_END: Int = 2

            private fun getReasonDescription(reason: @Reason Int, startUs: Long, endUs: Long): String {
                when (reason) {
                    REASON_INVALID_PERIOD_COUNT -> return "invalid period count"
                    REASON_NOT_SEEKABLE_TO_START -> return "not seekable to start"
                    REASON_START_EXCEEDS_END -> {
                        Assertions.checkState(startUs != C.TIME_UNSET && endUs != C.TIME_UNSET)
                        return "start exceeds end. Start time: " + startUs + ", End time: " + endUs
                    }

                    else -> return "unknown"
                }
            }
        }
    }

    private val startUs: Long
    private val endUs: Long
    private val enableInitialDiscontinuity: Boolean
    private val allowDynamicClippingUpdates: Boolean
    private val relativeToDefaultPosition: Boolean
    private val allowUnseekableMedia: Boolean
    private val mediaPeriods: ArrayList<ClippingMediaPeriod?>
    private val window: Timeline.Window

    private var clippingTimeline: ClippingTimeline? = null
    private var clippingError: IllegalClippingException? = null
    private var periodStartUs: Long = 0
    private var periodEndUs: Long = 0

    init {
        this.startUs = builder.startPositionUs
        this.endUs = builder.endPositionUs
        this.enableInitialDiscontinuity = builder.enableInitialDiscontinuity
        this.allowDynamicClippingUpdates = builder.allowDynamicClippingUpdates
        this.relativeToDefaultPosition = builder.relativeToDefaultPosition
        this.allowUnseekableMedia = builder.allowUnseekableMedia
        mediaPeriods = ArrayList<ClippingMediaPeriod?>()
        window = Timeline.Window()
    }

    override fun canUpdateMediaItem(mediaItem: MediaItem): Boolean {
        return getMediaItem().clippingConfiguration == mediaItem.clippingConfiguration
                && mediaSource.canUpdateMediaItem(mediaItem)
    }

    @Throws(IOException::class)
    override fun maybeThrowSourceInfoRefreshError() {
        if (clippingError != null) {
            throw clippingError!!
        }
        super.maybeThrowSourceInfoRefreshError()
    }

    override fun createPeriod(id: MediaPeriodId, allocator: Allocator, startPositionUs: Long): MediaPeriod {
        val mediaPeriod =
            ClippingMediaPeriod(
                mediaSource.createPeriod(id, allocator, startPositionUs),
                enableInitialDiscontinuity,
                periodStartUs,
                periodEndUs
            )
        mediaPeriods.add(mediaPeriod)
        return mediaPeriod
    }

    override fun releasePeriod(mediaPeriod: MediaPeriod) {
        Assertions.checkState(mediaPeriods.remove(mediaPeriod as ClippingMediaPeriod))
        mediaSource.releasePeriod(mediaPeriod.mediaPeriod)
        if (mediaPeriods.isEmpty() && !allowDynamicClippingUpdates) {
            refreshClippedTimeline(
                Assertions.checkNotNull(
                    clippingTimeline
                ).timeline
            )
        }
    }

    override fun releaseSourceInternal() {
        super.releaseSourceInternal()
        clippingError = null
        clippingTimeline = null
    }

    override fun onChildSourceInfoRefreshed(newTimeline: Timeline) {
        if (clippingError != null) {
            return
        }
        refreshClippedTimeline(newTimeline)
    }

    private fun refreshClippedTimeline(timeline: Timeline) {
        var windowStartUs: Long
        var windowEndUs: Long
        timeline.getWindow( /* windowIndex= */0, window)
        val windowPositionInPeriodUs = window.getPositionInFirstPeriodUs()
        if (clippingTimeline == null || mediaPeriods.isEmpty() || allowDynamicClippingUpdates) {
            windowStartUs = startUs
            windowEndUs = endUs
            if (relativeToDefaultPosition) {
                val windowDefaultPositionUs = window.getDefaultPositionUs()
                windowStartUs += windowDefaultPositionUs
                windowEndUs += windowDefaultPositionUs
            }
            periodStartUs = windowPositionInPeriodUs + windowStartUs
            periodEndUs =
                if (endUs == C.TIME_END_OF_SOURCE)
                    C.TIME_END_OF_SOURCE
                else
                    windowPositionInPeriodUs + windowEndUs
            val count = mediaPeriods.size
            for (i in 0..<count) {
                mediaPeriods.get(i)!!.updateClipping(periodStartUs, periodEndUs)
            }
        } else {
            // Keep window fixed at previous period position.
            windowStartUs = periodStartUs - windowPositionInPeriodUs
            windowEndUs =
                if (endUs == C.TIME_END_OF_SOURCE)
                    C.TIME_END_OF_SOURCE
                else
                    periodEndUs - windowPositionInPeriodUs
        }
        try {
            clippingTimeline =
                ClippingTimeline(timeline, windowStartUs, windowEndUs, allowUnseekableMedia)
        } catch (e: IllegalClippingException) {
            clippingError = e
            // The clipping error won't be propagated while we have existing MediaPeriods. Setting the
            // error at the MediaPeriods ensures it will be thrown as soon as possible.
            var i = 0
            while (i < mediaPeriods.size) {
                mediaPeriods.get(i)!!.setClippingError(clippingError)
                i++
            }
            return
        }
        refreshSourceInfo(clippingTimeline!!)
    }

    private class ClippingTimeline(timeline: Timeline, startUs: Long, endUs: Long, allowUnseekableMedia: Boolean) :
        ForwardingTimeline(timeline) {
        private val startUs: Long
        private val endUs: Long
        private val durationUs: Long
        private val isDynamic: Boolean

        init {
            var startUs = startUs
            var endUs = endUs
            if (endUs != C.TIME_END_OF_SOURCE && endUs < startUs) {
                throw IllegalClippingException(
                    IllegalClippingException.Companion.REASON_START_EXCEEDS_END, startUs, endUs
                )
            }
            if (timeline.getPeriodCount() != 1) {
                throw IllegalClippingException(IllegalClippingException.Companion.REASON_INVALID_PERIOD_COUNT)
            }
            val window = timeline.getWindow(0, Window())
            startUs = max(0, startUs)
            if (!allowUnseekableMedia && !window.isPlaceholder && startUs != 0L && !window.isSeekable) {
                throw IllegalClippingException(IllegalClippingException.Companion.REASON_NOT_SEEKABLE_TO_START)
            }
            endUs = if (endUs == C.TIME_END_OF_SOURCE) window.durationUs else max(0, endUs)
            if (window.durationUs != C.TIME_UNSET) {
                if (endUs > window.durationUs) {
                    endUs = window.durationUs
                }
                if (startUs > endUs) {
                    startUs = endUs
                }
            }
            this.startUs = startUs
            this.endUs = endUs
            durationUs = if (endUs == C.TIME_UNSET) C.TIME_UNSET else (endUs - startUs)
            isDynamic =
                window.isDynamic
                        && (endUs == C.TIME_UNSET
                        || (window.durationUs != C.TIME_UNSET && endUs == window.durationUs))
        }

        override fun getWindow(windowIndex: Int, window: Window, defaultPositionProjectionUs: Long): Window {
            timeline.getWindow( /* windowIndex= */0, window,  /* defaultPositionProjectionUs= */0)
            window.positionInFirstPeriodUs += startUs
            window.durationUs = durationUs
            window.isDynamic = isDynamic
            if (window.defaultPositionUs != C.TIME_UNSET) {
                window.defaultPositionUs = max(window.defaultPositionUs, startUs)
                window.defaultPositionUs =
                    if (endUs == C.TIME_UNSET) window.defaultPositionUs else min(window.defaultPositionUs, endUs)
                window.defaultPositionUs -= startUs
            }
            val startMs = Util.usToMs(startUs)
            if (window.presentationStartTimeMs != C.TIME_UNSET) {
                window.presentationStartTimeMs += startMs
            }
            if (window.windowStartTimeMs != C.TIME_UNSET) {
                window.windowStartTimeMs += startMs
            }
            return window
        }

        override fun getPeriod(periodIndex: Int, period: Period, setIds: Boolean): Period {
            timeline.getPeriod( /* periodIndex= */0, period, setIds)
            val positionInClippedWindowUs = period.getPositionInWindowUs() - startUs
            val periodDurationUs =
                if (durationUs == C.TIME_UNSET) C.TIME_UNSET else durationUs - positionInClippedWindowUs
            return period.set(
                period.id, period.uid,  /* windowIndex= */0, periodDurationUs, positionInClippedWindowUs
            )
        }

        val timeline: Timeline = super.timeline
    }
}
