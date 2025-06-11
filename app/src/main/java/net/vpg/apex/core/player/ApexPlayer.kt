package net.vpg.apex.core.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import net.vpg.apex.entities.ApexTrack
import java.io.File

class ApexPlayer {
    private val player: ExoPlayer
    private val queue = mutableListOf<ApexTrack>()
    private val _isPlaying = mutableStateOf(false)
    val isPlaying by _isPlaying
    private val _isBuffering = mutableStateOf(false)
    val isBuffering by _isBuffering
    private var currentIndex by mutableIntStateOf(-1)
    val nowPlaying get() = if (currentIndex < 0) ApexTrack.EMPTY else queue[currentIndex]
    private var prepared = false
    private val _isLooping = mutableStateOf(true)
    val isLooping by _isLooping
    fun stepUpLoop() {
        _isLooping.value = !_isLooping.value
        player.repeatMode = if (_isLooping.value && player.currentMediaItemIndex == 2)
            Player.REPEAT_MODE_ONE
        else
            Player.REPEAT_MODE_OFF
    }

    private val _isShuffling = mutableStateOf(false)
    var isShuffling by _isShuffling
    val loopStart get() = frameToUs(if (nowPlaying.loopStart == -1) 0 else nowPlaying.loopStart)
    val loopEnd get() = frameToUs(if (nowPlaying.loopEnd == -1) Int.MAX_VALUE else nowPlaying.loopEnd)
    val cacheDir: File
    var duration: Long = 0L

    val currentPosition: Long
        get() = when (player.currentMediaItemIndex) {
            1 -> player.currentPosition
            2 -> player.currentPosition + loopStart / 1000
            3 -> player.currentPosition + loopEnd / 1000
            else -> 0
        }

    private val mediaSourceFactory: DefaultMediaSourceFactory
    val handler: Handler

    @OptIn(UnstableApi::class)
    constructor(context: Context) {
        cacheDir = context.cacheDir
        mediaSourceFactory = DefaultMediaSourceFactory(context)
        val mainLooper = Looper.getMainLooper()
        handler = Handler(mainLooper)
        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLooper(mainLooper)
            .build()
        player.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                _isPlaying.value = playWhenReady
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _isBuffering.value = true
                    }

                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        if (player.currentMediaItemIndex == 0) {
                            duration = player.duration
                            player.seekTo(1, 0)
                        }
                    }

                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                        prepared = false
                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                player.repeatMode = if (player.currentMediaItemIndex == 2 && isLooping)
                    Player.REPEAT_MODE_ONE
                else
                    Player.REPEAT_MODE_OFF
            }
        })
    }

    fun play(track: ApexTrack) {
        for (i in queue.size - 1 downTo currentIndex + 1)
            queue.removeAt(currentIndex + 1)
        queue(track)
        currentIndex++
        playCurrentTrack()
    }

    @OptIn(UnstableApi::class)
    fun playCurrentTrack() {
        val mediaUri = nowPlaying.downloadedFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: nowPlaying.cacheFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: nowPlaying.url

        val mediaItem = MediaItem.fromUri(mediaUri)

        // Create a base MediaSource
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        // Wrap in ClippingMediaSource with the loop start and end positions
        val preLoop = mediaSource.clip(0, loopStart)
        val loop = mediaSource.clip(loopStart, loopEnd)
        val postLoop = mediaSource.clip(loopEnd)

        player.setMediaItem(mediaItem)
        player.addMediaSource(preLoop)
        player.addMediaSource(loop)
        player.addMediaSource(postLoop)
        player.prepare()
        player.play()
        prepared = true
        duration = 0
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
        if (player.isPlaying)
            player.pause()
        else if (prepared)
            player.play()
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

    fun canGoPrevious() = currentIndex > 0

    fun previousTrack() {
        if (!canGoPrevious()) return
        currentIndex--
        playCurrentTrack()
    }

    fun canGoNext() = currentIndex < queue.size - 1

    fun nextTrack() {
        if (!canGoNext()) return
        currentIndex++
        playCurrentTrack()
    }

    @OptIn(UnstableApi::class)
    fun seekTo(positionMs: Long) = handler.post {
        val position = positionMs * 1000
        if (position < loopStart) {
            player.seekTo(1, position / 1000)
        } else if (position < loopEnd) {
            player.seekTo(2, (position - loopStart) / 1000)
        } else {
            player.seekTo(3, (position - loopEnd) / 1000)
        }
    }
}
