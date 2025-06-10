package net.vpg.apex.core.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
    private val _isLooping = mutableStateOf(false)
    var isLooping by _isLooping
    private val _isShuffling = mutableStateOf(false)
    var isShuffling by _isShuffling
    val loopStart get() = if (nowPlaying.loopStart == -1) 0 else nowPlaying.loopStart
    val loopEnd get() = if (nowPlaying.loopEnd == -1) nowPlaying.frameLength else nowPlaying.loopEnd
    val cacheDir: File
    val duration get() = player.duration
    val currentPosition
        @OptIn(UnstableApi::class)
        get() = player.currentPosition

    //
//    @UnstableApi
//    private val looper = LoopingAudioProcessor(this)
    private val mediaSourceFactory: DefaultMediaSourceFactory
    val handler: Handler

    @OptIn(UnstableApi::class)
    constructor(context: Context) {
//        // Create a custom RenderersFactory that uses our audio processors
//        val renderersFactory = object : DefaultRenderersFactory(context) {
//            override fun buildAudioSink(
//                context: Context,
//                enableFloatOutput: Boolean,
//                enableAudioTrackPlaybackParams: Boolean
//            ) = DefaultAudioSink.Builder(context)
//                .setAudioProcessors(arrayOf(looper))
//                .setEnableFloatOutput(enableFloatOutput)
//                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
//                .build()
//        }
        cacheDir = context.cacheDir
        mediaSourceFactory = DefaultMediaSourceFactory(context)
        val mainLooper = Looper.getMainLooper()
        handler = Handler(mainLooper)
        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
//            .setRenderersFactory(renderersFactory)
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
        val mediaUri = nowPlaying.downloadedFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: nowPlaying.cacheFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: nowPlaying.url

        val mediaItem = MediaItem.fromUri(mediaUri)

        // Create a base MediaSource
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        // Wrap in ClippingMediaSource with the loop start and end positions
//        val preLoop = createClippingMediaSource(mediaSource, 0, loopStart)
        val loop = LoopingMediaSource(createClippingMediaSource(mediaSource, loopStart, loopEnd))
//        val postLoop = createClippingMediaSource(mediaSource, loopEnd)

//        val clippingMediaSource = ConcatenatingMediaSource(preLoop, /*loop,*/ postLoop)
        player.setMediaSource(loop)
        player.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
        prepared = true
    }

    @OptIn(UnstableApi::class)
    private fun createClippingMediaSource(
        mediaSource: MediaSource,
        startFrame: Int,
        endFrame: Int = -1
    ): ClippingMediaSource {
        return ClippingMediaSource.Builder(mediaSource)
            .setStartPositionUs((startFrame.toFloat() / nowPlaying.sampleRate * 1000_000).toLong())
            .setEndPositionUs(((if (endFrame == -1) C.TIME_END_OF_SOURCE else endFrame).toFloat() / nowPlaying.sampleRate * 1000_000).toLong())
            .build()
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

    @OptIn(UnstableApi::class)
    fun seekTo(positionMs: Long) = handler.post { player.seekTo(positionMs) }
}
