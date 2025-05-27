package net.vpg.apex.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.DefaultAudioSink
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
    var isLooping by _isLooping
    val loopStart get() = if (nowPlaying.loopStart == -1) 0 else nowPlaying.loopStart
    val loopEnd get() = if (nowPlaying.loopEnd == -1) nowPlaying.frameLength else nowPlaying.loopEnd
    val cacheDir: File

    @OptIn(UnstableApi::class)
    constructor(context: Context) {
        // Create a custom RenderersFactory that uses our audio processors
        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ) = DefaultAudioSink.Builder(context)
                .setAudioProcessors(arrayOf(LoopingAudioProcessor(this@ApexPlayer)))
                .setEnableFloatOutput(enableFloatOutput)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .build()
        }

        cacheDir = context.cacheDir
        player = ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
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
                    }

                    Player.STATE_ENDED -> {
                        _isPlaying.value = false
                        prepared = false
                    }

                    Player.STATE_IDLE -> {
                    }
                }
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
        player.setMediaItem(nowPlaying.toMediaItem(cacheDir))
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
}
