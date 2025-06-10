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
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.*
import androidx.media3.exoplayer.source.MediaSource.MediaPeriodId
import androidx.media3.exoplayer.upstream.Allocator

@UnstableApi
class LoopingMediaSource(childSource: MediaSource) :
    WrappingMediaSource(MaskingMediaSource(childSource, false)) {
    private val childMediaPeriodIdToMediaPeriodId = mutableMapOf<MediaPeriodId, MediaPeriodId>()
    private val mediaPeriodToChildMediaPeriodId = mutableMapOf<MediaPeriod, MediaPeriodId>()

    override fun getInitialTimeline(): Timeline = InfinitelyLoopingTimeline((mediaSource as MaskingMediaSource).timeline)

    override fun isSingleWindow() = false

    override fun createPeriod(id: MediaPeriodId, allocator: Allocator, startPositionUs: Long) =
        mediaSource.createPeriod(id, allocator, startPositionUs)

    override fun releasePeriod(mediaPeriod: MediaPeriod) {
        mediaSource.releasePeriod(mediaPeriod)
        val childMediaPeriodId = mediaPeriodToChildMediaPeriodId.remove(mediaPeriod)
        if (childMediaPeriodId != null) {
            childMediaPeriodIdToMediaPeriodId.remove(childMediaPeriodId)
        }
    }

    override fun onChildSourceInfoRefreshed(newTimeline: Timeline) {
        val loopingTimeline = InfinitelyLoopingTimeline(newTimeline)
        refreshSourceInfo(loopingTimeline)
    }

    override fun getMediaPeriodIdForChildMediaPeriodId(mediaPeriodId: MediaPeriodId) = mediaPeriodId

    private class InfinitelyLoopingTimeline(timeline: Timeline) : ForwardingTimeline(timeline) {
        override fun getNextWindowIndex(
            windowIndex: Int,
            repeatMode: @Player.RepeatMode Int,
            shuffleModeEnabled: Boolean
        ): Int {
            val childNextWindowIndex = timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled)
            return if (childNextWindowIndex == C.INDEX_UNSET)
                getFirstWindowIndex(shuffleModeEnabled)
            else
                childNextWindowIndex
        }

        override fun getPreviousWindowIndex(
            windowIndex: Int,
            repeatMode: @Player.RepeatMode Int,
            shuffleModeEnabled: Boolean
        ): Int {
            val childPreviousWindowIndex = timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled)
            return if (childPreviousWindowIndex == C.INDEX_UNSET)
                getLastWindowIndex(shuffleModeEnabled)
            else
                childPreviousWindowIndex
        }
    }
}
