package net.vpg.apex.core.player

import android.content.Context
import android.os.Handler
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.MediaSource
import net.vpg.apex.core.ApexTrackContextDynamic
import net.vpg.apex.core.PlayHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexTrackContext
import kotlin.math.abs

@OptIn(UnstableApi::class)
class ApexPlayer(
    context: Context,
    val playHistory: PlayHistory,
    val mediaSourceFactory: MediaSource.Factory,
) : ForwardingPlayer(
    ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()
), Player.Listener {
    private val exoplayer = wrappedPlayer as ExoPlayer
    private var shuffleOrder: ShuffleOrderContext? = null
    private var originalContextState by mutableStateOf(ApexTrackContext.EMPTY)
    var currentContext
        set(value) = run { originalContextState = value }
        get() = shuffleOrder ?: originalContextState

    private var playingState by mutableStateOf(false)
    private var bufferingState by mutableStateOf(false)
    private var loopingState by mutableStateOf(true)
    private var shuffleState by mutableStateOf(false)
    var currentIndex by mutableIntStateOf(-1)
        private set
    private var durationState by mutableLongStateOf(0)

    override fun isPlaying() = playingState
    val isBuffering get() = bufferingState
    val nowPlaying get() = if (currentIndex < 0) ApexTrack.Companion.EMPTY else currentContext.tracks[currentIndex]
    val isLooping get() = loopingState
    val isShuffling get() = shuffleState
    override fun getDuration() = durationState

    private var isPrepared = false
    private var isEnded = false
    private val loopStart get() = if (nowPlaying.loopStart == -1) 0L else frameToUs(nowPlaying.loopStart)
    private val loopEnd get() = if (nowPlaying.loopEnd == -1) Long.MAX_VALUE else frameToUs(nowPlaying.loopEnd)

    override fun getCurrentPosition() = when (currentMediaItemIndex) {
        1 -> exoplayer.currentPosition
        2 -> exoplayer.currentPosition + loopStart / 1000
        3 -> exoplayer.currentPosition + loopEnd / 1000
        else -> 0
    }

    private val handler = Handler(applicationLooper)

    init {
        addListener(this)
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        playingState = playWhenReady
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            STATE_BUFFERING -> {
                bufferingState = true
            }

            STATE_READY -> {
                bufferingState = false
                playingState = true
                isEnded = false
                if (currentMediaItemIndex == 0) {
                    durationState = exoplayer.duration
                    seekTo(1, 0)
                }
            }

            STATE_ENDED -> {
                // After many efforts, it was discovered that
                // if you tried to loop using REPEAT_MODE_ALL after post-loop clip,
                // Then it'll seek to MediaItem 0, which is the base track.
                // But we want it to be MediaItem 1, which is the pre-loop clip.
                // When we try to seek to MediaItem 1, there's a slight delay
                // which causes the player to play a few milliseconds of the base track
                // and then seek to MediaItem 1, which is not what we want.
                // So we manually handle the looping here.
                if (currentMediaItemIndex == 3 && isLooping)
                    seekTo(1, 0) // manual looping
                else {
                    playingState = false
                    isEnded = true
                    seekToNext()
                }
            }

            STATE_IDLE -> {
                isPrepared = false
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = updatePlayerRepeatMode()

    fun play(delta: Int) {
        if (currentContext.tracks[currentIndex + delta] == ApexTrack.EMPTY)
            play(delta = delta + (delta / abs(delta)))
        else
            play(trackIndex = currentIndex + delta, currentContext)
    }

    @OptIn(UnstableApi::class)
    fun play(trackIndex: Int, context: ApexTrackContext, updateHistory: Boolean = true) {
        currentIndex = trackIndex
        currentContext = context
        if (shuffleState) {
            updateShuffleOrder()
        }

        if (updateHistory) {
            playHistory.addTrack(nowPlaying, currentContext)
        }

        MediaItem.Builder()
            .setUri(nowPlaying.url.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(nowPlaying.title)
                    .setArtist(nowPlaying.uploader.name)
                    .setArtworkUri(nowPlaying.album.albumArtUrl?.toUri())
                    .build()
            )
            .build()
            .let { mediaSourceFactory.createMediaSource(it) }
            .let { mediaItem ->
                listOf(
                    mediaItem, // Base Track
                    mediaItem.clip(0, loopStart), // Pre-loop/Intro
                    mediaItem.clip(loopStart, loopEnd), // Loop Section
                    mediaItem.clip(loopEnd) // Post-loop/Outro
                )
            }
            .also { exoplayer.setMediaSources(it) }
        prepare()
        play()
        isPrepared = true
        durationState = 0
    }

    @OptIn(UnstableApi::class)
    private fun MediaSource.clip(
        startFrame: Long,
        endFrame: Long = Long.MAX_VALUE,
    ) = ClippingMediaSource.Builder(this)
        .setStartPositionUs(startFrame)
        .setEndPositionUs(endFrame)
        .setEnableInitialDiscontinuity(false)
        .build()

    private fun frameToUs(frame: Int) = (frame.toFloat() / nowPlaying.sampleRate * 1000_000).toLong()

    fun togglePlayPause() {
        if (isPlaying)
            pause()
        else if (isEnded)
            seekTo(0)
        else if (isPrepared)
            play()
        else
            play(currentIndex, currentContext, updateHistory = false)
    }

    override fun seekToPrevious() {
        if (nowPlaying == ApexTrack.EMPTY) return
        val isFirstIndex = currentIndex == 0 ||
                currentContext.tracks.subList(0, currentIndex).all { it == ApexTrack.EMPTY }
        if (isFirstIndex || currentPosition > maxSeekToPreviousPosition)
            seekTo(0)
        else
            play(delta = -1)
    }

    override fun hasNextMediaItem() = currentIndex < currentContext.tracks.size - 1
            && currentContext.tracks.subList(currentIndex + 1, currentContext.tracks.size)
        .any { it != ApexTrack.EMPTY }

    override fun seekToNext() {
        if (!hasNextMediaItem()) return
        play(delta = +1)
    }

    fun toggleShuffling() {
        shuffleState = !shuffleState
        updateShuffleOrder()
    }

    fun updateShuffleOrder() {
        if (currentIndex == -1) return
        if (shuffleState) {
            if (shuffleOrder?.wrappedContext != originalContextState)
                shuffleOrder = ShuffleOrderContext(originalContextState)
            currentIndex = shuffleOrder!!.tracks.indexOf(shuffleOrder!!.wrappedContext.tracks[currentIndex])
        } else {
            currentIndex = shuffleOrder!!.wrappedContext.tracks.indexOf(nowPlaying)
            shuffleOrder = null
        }
    }

    fun stepUpLoop() {
        loopingState = !loopingState
        updatePlayerRepeatMode()
    }

    private fun updatePlayerRepeatMode() {
        repeatMode = if (loopingState && currentMediaItemIndex == 2)
            REPEAT_MODE_ONE
        else
            REPEAT_MODE_OFF
    }

    @OptIn(UnstableApi::class)
    override fun seekTo(positionMs: Long) {
        handler.post {
            val position = positionMs * 1000
            if (position < loopStart) {
                seekTo(1, position / 1000)
            } else if (position < loopEnd) {
                seekTo(2, (position - loopStart) / 1000)
            } else {
                seekTo(3, (position - loopEnd) / 1000)
            }
        }
    }

    private inner class ShuffleOrderContext(val wrappedContext: ApexTrackContext) :
        ApexTrackContextDynamic(
            name = wrappedContext.name,
            tracks = wrappedContext.tracks.shuffled()
        ) {
        override operator fun equals(other: Any?) = wrappedContext == other
        override fun hashCode() = wrappedContext.hashCode()
    }
}