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
import net.vpg.apex.entities.ApexTrack

@OptIn(UnstableApi::class)
class ApexPlayer(
    val context: Context,
    val mediaSourceFactory: MediaSource.Factory,
) : ForwardingPlayer(
    ExoPlayer.Builder(context)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()
), Player.Listener {
    private val exoplayer = wrappedPlayer as ExoPlayer
    private val queue = mutableListOf<ApexTrack>()

    private var playingState by mutableStateOf(false)
    private var bufferingState by mutableStateOf(false)
    private var loopingState by mutableStateOf(true)
    private var shuffleState by mutableStateOf(false)
    private var currentIndex by mutableIntStateOf(-1)
    private var durationState by mutableLongStateOf(0)

    override fun isPlaying() = playingState
    val isBuffering get() = bufferingState
    val nowPlaying get() = if (currentIndex < 0) ApexTrack.Companion.EMPTY else queue[currentIndex]
    val isLooping get() = loopingState
    val isShuffling get() = shuffleState
    override fun getDuration() = durationState

    private var prepared = false
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
                if (currentMediaItemIndex == 0) {
                    durationState = exoplayer.duration
                    seekTo(1, 0)
                }
            }

            STATE_ENDED -> {
                playingState = false
            }

            STATE_IDLE -> {
                prepared = false
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = updatePlayerRepeatMode()

    fun play(track: ApexTrack) {
        for (i in queue.size - 1 downTo currentIndex + 1)
            queue.removeAt(currentIndex + 1)
        queue(track)
        currentIndex++
        playCurrentTrack()
    }

    @OptIn(UnstableApi::class)
    fun playCurrentTrack() {
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
        prepared = true
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
        else if (prepared)
            play()
        else
            playCurrentTrack()
    }

    fun queue(track: ApexTrack) {
        queue.add(track)
    }

    fun removeTrackAt(index: Int) {
        if (index < 0 || index >= queue.size) throw IllegalStateException()
        queue.removeAt(index)
    }

    override fun seekToPrevious() {
        if (nowPlaying == ApexTrack.EMPTY) return
        if (currentIndex == 0 || currentPosition > maxSeekToPreviousPosition)
            seekTo(0)
        else {
            currentIndex--
            playCurrentTrack()
        }
    }

    override fun hasNextMediaItem() = currentIndex < queue.size - 1

    override fun seekToNext() {
        if (!hasNextMediaItem()) return
        currentIndex++
        playCurrentTrack()
    }

    fun toggleShuffling() {
        shuffleState = !shuffleState
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
}