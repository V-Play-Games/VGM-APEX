package net.vpg.apex.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class ApexPlayer {
    private val player: ExoPlayer
    private val queue = mutableListOf<ApexTrack>()
    private val _isPlaying = mutableStateOf(false)
    val isPlaying by _isPlaying
    private val _isBuffering = mutableStateOf(false)
    val isBuffering by _isBuffering
    private val currentIndex = mutableIntStateOf(-1)
    val nowPlaying: ApexTrack
        get() = if (currentIndex.intValue < 0) ApexTrack.EMPTY else queue[currentIndex.intValue]
    private var prepared = false
    val isLooping = mutableStateOf(true)
    private var lastPositionMs: Long = 0
    private val loopStart = mutableIntStateOf(0)
    private val loopEnd = mutableIntStateOf(0)

    @OptIn(UnstableApi::class)
    constructor(context: Context) {
        player = ExoPlayer.Builder(context).build()
        player.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                _isPlaying.value = playWhenReady
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _isBuffering.value = true
                    }

                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        loopStart.intValue = (nowPlaying.loopStart * 1000 / player.audioFormat!!.sampleRate).also { println(it) }
                        loopEnd.intValue = (nowPlaying.loopEnd * 1000 / player.audioFormat!!.sampleRate).also { println(it) }
                    }

                    Player.STATE_ENDED -> {
                        if (isLooping.value) {
                            player.seekTo(loopStart.intValue.toLong())
                            player.play()
                        } else {
                            _isPlaying.value = false
                            prepared = false
                        }
                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }
        })
        executePeriodically {
            if (isLooping.value &&
                player.isPlaying &&
                player.currentPosition.also { lastPositionMs = it } >= loopEnd.intValue &&
                lastPositionMs < loopEnd.intValue) {
                player.seekTo(loopStart.intValue.toLong())
            }
        }
    }

    fun executePeriodically(
        intervalMs: Long = 100L,
        action: () -> Unit
    ) = Handler(Looper.getMainLooper()).let { handler ->
        handler.postDelayed(object : Runnable {
            override fun run() {
                action()
                handler.postDelayed(this, intervalMs)
            }
        }, intervalMs)
    }

    fun play(track: ApexTrack) {
        for (i in queue.size - 1 downTo currentIndex.intValue + 1)
            queue.removeAt(currentIndex.intValue + 1)
        queue(track)
        currentIndex.intValue++
        playCurrentTrack()
    }

    @OptIn(UnstableApi::class)
    fun playCurrentTrack() {
        player.setMediaItem(nowPlaying.mediaItem)
        player.prepare()
        player.play()
        prepared = true
    }

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

    fun canGoPrevious() = currentIndex.intValue > 0

    fun previousTrack() {
        if (!canGoPrevious()) return
        currentIndex.intValue--
        playCurrentTrack()
    }

    fun canGoNext() = currentIndex.intValue < queue.size - 1

    fun nextTrack() {
        if (!canGoNext()) return
        currentIndex.intValue++
        playCurrentTrack()
    }

}
