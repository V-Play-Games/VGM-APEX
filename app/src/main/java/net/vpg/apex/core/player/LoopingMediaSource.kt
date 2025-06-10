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
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.AbstractConcatenatedTimeline
import androidx.media3.exoplayer.source.*
import androidx.media3.exoplayer.source.MediaSource.MediaPeriodId
import androidx.media3.exoplayer.source.ShuffleOrder.UnshuffledShuffleOrder
import androidx.media3.exoplayer.upstream.Allocator

@UnstableApi
class LoopingMediaSource @JvmOverloads constructor(childSource: MediaSource, loopCount: Int = Int.Companion.MAX_VALUE) :
    WrappingMediaSource(MaskingMediaSource(childSource,  /* useLazyPreparation= */false)) {
    private val loopCount: Int
    private val childMediaPeriodIdToMediaPeriodId: MutableMap<MediaPeriodId?, MediaPeriodId?>
    private val mediaPeriodToChildMediaPeriodId: MutableMap<MediaPeriod?, MediaPeriodId?>

    init {
        Assertions.checkArgument(loopCount > 0)
        this.loopCount = loopCount
        childMediaPeriodIdToMediaPeriodId = HashMap<MediaPeriodId?, MediaPeriodId?>()
        mediaPeriodToChildMediaPeriodId = HashMap<MediaPeriod?, MediaPeriodId?>()
    }

    override fun getInitialTimeline(): Timeline {
        val maskingMediaSource = mediaSource as MaskingMediaSource
        return if (loopCount != Int.Companion.MAX_VALUE)
            LoopingTimeline(maskingMediaSource.getTimeline(), loopCount)
        else
            InfinitelyLoopingTimeline(maskingMediaSource.getTimeline())
    }

    override fun isSingleWindow(): Boolean {
        return false
    }

    override fun createPeriod(id: MediaPeriodId, allocator: Allocator, startPositionUs: Long): MediaPeriod {
        if (loopCount == Int.Companion.MAX_VALUE) {
            return mediaSource.createPeriod(id, allocator, startPositionUs)
        }
        val childPeriodUid = AbstractConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(id.periodUid)
        val childMediaPeriodId = id.copyWithPeriodUid(childPeriodUid)
        childMediaPeriodIdToMediaPeriodId.put(childMediaPeriodId, id)
        val mediaPeriod =
            mediaSource.createPeriod(childMediaPeriodId, allocator, startPositionUs)
        mediaPeriodToChildMediaPeriodId.put(mediaPeriod, childMediaPeriodId)
        return mediaPeriod
    }

    override fun releasePeriod(mediaPeriod: MediaPeriod) {
        mediaSource.releasePeriod(mediaPeriod)
        val childMediaPeriodId = mediaPeriodToChildMediaPeriodId.remove(mediaPeriod)
        if (childMediaPeriodId != null) {
            childMediaPeriodIdToMediaPeriodId.remove(childMediaPeriodId)
        }
    }

    override fun onChildSourceInfoRefreshed(newTimeline: Timeline) {
        val loopingTimeline =
            if (loopCount != Int.Companion.MAX_VALUE)
                LoopingTimeline(newTimeline, loopCount)
            else
                InfinitelyLoopingTimeline(newTimeline)
        refreshSourceInfo(loopingTimeline)
    }

    override fun getMediaPeriodIdForChildMediaPeriodId(mediaPeriodId: MediaPeriodId): MediaPeriodId? {
        return if (loopCount != Int.Companion.MAX_VALUE)
            childMediaPeriodIdToMediaPeriodId.get(mediaPeriodId)
        else
            mediaPeriodId
    }

    private class LoopingTimeline(private val childTimeline: Timeline, private val loopCount: Int) :
        AbstractConcatenatedTimeline( /* isAtomic= */false, UnshuffledShuffleOrder(
            loopCount
        )
        ) {
        private val childPeriodCount: Int
        private val childWindowCount: Int

        init {
            childPeriodCount = childTimeline.getPeriodCount()
            childWindowCount = childTimeline.getWindowCount()
            if (childPeriodCount > 0) {
                Assertions.checkState(
                    loopCount <= Int.Companion.MAX_VALUE / childPeriodCount,
                    "LoopingMediaSource contains too many periods"
                )
            }
        }

        override fun getWindowCount(): Int {
            return childWindowCount * loopCount
        }

        override fun getPeriodCount(): Int {
            return childPeriodCount * loopCount
        }

        override fun getChildIndexByPeriodIndex(periodIndex: Int): Int {
            return periodIndex / childPeriodCount
        }

        override fun getChildIndexByWindowIndex(windowIndex: Int): Int {
            return windowIndex / childWindowCount
        }

        override fun getChildIndexByChildUid(childUid: Any): Int {
            if (childUid !is Int) {
                return C.INDEX_UNSET
            }
            return childUid
        }

        override fun getTimelineByChildIndex(childIndex: Int): Timeline {
            return childTimeline
        }

        override fun getFirstPeriodIndexByChildIndex(childIndex: Int): Int {
            return childIndex * childPeriodCount
        }

        override fun getFirstWindowIndexByChildIndex(childIndex: Int): Int {
            return childIndex * childWindowCount
        }

        override fun getChildUidByChildIndex(childIndex: Int): Any {
            return childIndex
        }
    }

    private class InfinitelyLoopingTimeline(timeline: Timeline) : ForwardingTimeline(timeline) {
        override fun getNextWindowIndex(
            windowIndex: Int, repeatMode: @Player.RepeatMode Int, shuffleModeEnabled: Boolean
        ): Int {
            val childNextWindowIndex =
                timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled)
            return if (childNextWindowIndex == C.INDEX_UNSET)
                getFirstWindowIndex(shuffleModeEnabled)
            else
                childNextWindowIndex
        }

        override fun getPreviousWindowIndex(
            windowIndex: Int, repeatMode: @Player.RepeatMode Int, shuffleModeEnabled: Boolean
        ): Int {
            val childPreviousWindowIndex =
                timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled)
            return if (childPreviousWindowIndex == C.INDEX_UNSET)
                getLastWindowIndex(shuffleModeEnabled)
            else
                childPreviousWindowIndex
        }
    }
}
