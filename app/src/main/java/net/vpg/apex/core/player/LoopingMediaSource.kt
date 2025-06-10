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
package net.vpg.apex.core.player;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.AbstractConcatenatedTimeline;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.*;
import androidx.media3.exoplayer.source.ShuffleOrder.UnshuffledShuffleOrder;
import androidx.media3.exoplayer.upstream.Allocator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Loops a {@link MediaSource} a specified number of times.
 */
@UnstableApi
public final class LoopingMediaSource extends WrappingMediaSource {

    private final int loopCount;
    private final Map<MediaPeriodId, MediaPeriodId> childMediaPeriodIdToMediaPeriodId;
    private final Map<MediaPeriod, MediaPeriodId> mediaPeriodToChildMediaPeriodId;

    /**
     * Loops the provided source indefinitely. Note that it is usually better to use {@link
     * ExoPlayer#setRepeatMode(int)}.
     *
     * @param childSource The {@link MediaSource} to loop.
     */
    public LoopingMediaSource(MediaSource childSource) {
        this(childSource, Integer.MAX_VALUE);
    }

    /**
     * Loops the provided source a specified number of times.
     *
     * @param childSource The {@link MediaSource} to loop.
     * @param loopCount   The desired number of loops. Must be strictly positive.
     */
    public LoopingMediaSource(MediaSource childSource, int loopCount) {
        super(new MaskingMediaSource(childSource, /* useLazyPreparation= */ false));
        Assertions.checkArgument(loopCount > 0);
        this.loopCount = loopCount;
        childMediaPeriodIdToMediaPeriodId = new HashMap<>();
        mediaPeriodToChildMediaPeriodId = new HashMap<>();
    }

    @Override
    public @NotNull Timeline getInitialTimeline() {
        MaskingMediaSource maskingMediaSource = (MaskingMediaSource) mediaSource;
        return loopCount != Integer.MAX_VALUE
            ? new LoopingTimeline(maskingMediaSource.getTimeline(), loopCount)
            : new InfinitelyLoopingTimeline(maskingMediaSource.getTimeline());
    }

    @Override
    public boolean isSingleWindow() {
        return false;
    }

    @Override
    public @NotNull MediaPeriod createPeriod(@NotNull MediaPeriodId id, @NotNull Allocator allocator, long startPositionUs) {
        if (loopCount == Integer.MAX_VALUE) {
            return mediaSource.createPeriod(id, allocator, startPositionUs);
        }
        Object childPeriodUid = LoopingTimeline.getChildPeriodUidFromConcatenatedUid(id.periodUid);
        MediaPeriodId childMediaPeriodId = id.copyWithPeriodUid(childPeriodUid);
        childMediaPeriodIdToMediaPeriodId.put(childMediaPeriodId, id);
        MediaPeriod mediaPeriod =
            mediaSource.createPeriod(childMediaPeriodId, allocator, startPositionUs);
        mediaPeriodToChildMediaPeriodId.put(mediaPeriod, childMediaPeriodId);
        return mediaPeriod;
    }

    @Override
    public void releasePeriod(@NotNull MediaPeriod mediaPeriod) {
        mediaSource.releasePeriod(mediaPeriod);
        @Nullable
        MediaPeriodId childMediaPeriodId = mediaPeriodToChildMediaPeriodId.remove(mediaPeriod);
        if (childMediaPeriodId != null) {
            childMediaPeriodIdToMediaPeriodId.remove(childMediaPeriodId);
        }
    }

    @Override
    protected void onChildSourceInfoRefreshed(@NotNull Timeline newTimeline) {
        Timeline loopingTimeline =
            loopCount != Integer.MAX_VALUE
                ? new LoopingTimeline(newTimeline, loopCount)
                : new InfinitelyLoopingTimeline(newTimeline);
        refreshSourceInfo(loopingTimeline);
    }

    @Override
    @Nullable
    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(@NotNull MediaPeriodId mediaPeriodId) {
        return loopCount != Integer.MAX_VALUE
            ? childMediaPeriodIdToMediaPeriodId.get(mediaPeriodId)
            : mediaPeriodId;
    }

    private static final class LoopingTimeline extends AbstractConcatenatedTimeline {

        private final Timeline childTimeline;
        private final int childPeriodCount;
        private final int childWindowCount;
        private final int loopCount;

        public LoopingTimeline(Timeline childTimeline, int loopCount) {
            super(/* isAtomic= */ false, new UnshuffledShuffleOrder(loopCount));
            this.childTimeline = childTimeline;
            childPeriodCount = childTimeline.getPeriodCount();
            childWindowCount = childTimeline.getWindowCount();
            this.loopCount = loopCount;
            if (childPeriodCount > 0) {
                Assertions.checkState(
                    loopCount <= Integer.MAX_VALUE / childPeriodCount,
                    "LoopingMediaSource contains too many periods");
            }
        }

        @Override
        public int getWindowCount() {
            return childWindowCount * loopCount;
        }

        @Override
        public int getPeriodCount() {
            return childPeriodCount * loopCount;
        }

        @Override
        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return periodIndex / childPeriodCount;
        }

        @Override
        protected int getChildIndexByWindowIndex(int windowIndex) {
            return windowIndex / childWindowCount;
        }

        @Override
        protected int getChildIndexByChildUid(@NotNull Object childUid) {
            if (!(childUid instanceof Integer)) {
                return C.INDEX_UNSET;
            }
            return (Integer) childUid;
        }

        @Override
        protected @NotNull Timeline getTimelineByChildIndex(int childIndex) {
            return childTimeline;
        }

        @Override
        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return childIndex * childPeriodCount;
        }

        @Override
        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return childIndex * childWindowCount;
        }

        @Override
        protected @NotNull Object getChildUidByChildIndex(int childIndex) {
            return childIndex;
        }
    }

    private static final class InfinitelyLoopingTimeline extends ForwardingTimeline {

        public InfinitelyLoopingTimeline(Timeline timeline) {
            super(timeline);
        }

        @Override
        public int getNextWindowIndex(
            int windowIndex, @Player.RepeatMode int repeatMode, boolean shuffleModeEnabled) {
            int childNextWindowIndex =
                timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childNextWindowIndex == C.INDEX_UNSET
                ? getFirstWindowIndex(shuffleModeEnabled)
                : childNextWindowIndex;
        }

        @Override
        public int getPreviousWindowIndex(
            int windowIndex, @Player.RepeatMode int repeatMode, boolean shuffleModeEnabled) {
            int childPreviousWindowIndex =
                timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childPreviousWindowIndex == C.INDEX_UNSET
                ? getLastWindowIndex(shuffleModeEnabled)
                : childPreviousWindowIndex;
        }
    }
}
