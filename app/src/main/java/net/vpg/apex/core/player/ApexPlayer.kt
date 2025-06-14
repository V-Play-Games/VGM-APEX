package net.vpg.apex.core.player

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import net.vpg.apex.core.NotificationMaker
import net.vpg.apex.core.player.MediaNotificationService.Companion.NOTIFICATION_ID
import net.vpg.apex.entities.ApexTrack
import java.io.File

@OptIn(UnstableApi::class)
class ApexPlayer(
    val context: Context,
    val cacheDir: File,
    val mediaSourceFactory: MediaSource.Factory,
    playerBuilder: ExoPlayer.Builder
) : ForwardingPlayer(playerBuilder.setMediaSourceFactory(mediaSourceFactory).build()), Player.Listener {
    private val notificationMaker = NotificationMaker()
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
    private val loopStart get() = frameToUs(if (nowPlaying.loopStart == -1) 0 else nowPlaying.loopStart)
    private val loopEnd get() = frameToUs(if (nowPlaying.loopEnd == -1) Int.MAX_VALUE else nowPlaying.loopEnd)

    override fun getCurrentPosition() = when (currentMediaItemIndex) {
        1 -> exoplayer.currentPosition
        2 -> exoplayer.currentPosition + loopStart / 1000
        3 -> exoplayer.currentPosition + loopEnd / 1000
        else -> 0
    }

    private val handler = Handler(applicationLooper)

    @OptIn(UnstableApi::class)
    constructor(context: Context) : this(
        context = context,
        cacheDir = context.cacheDir,
        mediaSourceFactory = DefaultMediaSourceFactory(
            CacheDataSource.Factory()
                .setCache(
                    SimpleCache(
                        File(context.cacheDir, "exo_cache"),
                        LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024), // 512 MB
                        StandaloneDatabaseProvider(context)
                    )
                )
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        ),
        playerBuilder = ExoPlayer.Builder(context)
            .setLooper(Looper.getMainLooper())
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
    )

    init {
        addListener(this)
    }

    private var notificationManager: NotificationManager? = null

    fun setNotificationManager(manager: NotificationManager) {
        notificationManager = manager
    }

    fun updateNotification() {
        notificationManager?.notify(NOTIFICATION_ID, notificationMaker.createNotification(context))
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        playingState = playWhenReady
        updateNotification()
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
                prepared = false
            }

            STATE_IDLE -> {
            }
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = updatePlayerRepeatMode()
    fun play(track: ApexTrack) {
        if (nowPlaying == ApexTrack.EMPTY) {
            val intent = Intent(context, MediaNotificationService::class.java)
            context.startForegroundService(intent)
        }
        for (i in queue.size - 1 downTo currentIndex + 1)
            queue.removeAt(currentIndex + 1)
        queue(track)
        currentIndex++
        playCurrentTrack()
    }

    @OptIn(UnstableApi::class)
    fun playCurrentTrack() {
        val mediaUri = nowPlaying.downloadedFile(cacheDir).takeIf { it.exists() }?.toUri()
            ?: nowPlaying.url.toUri()

        val mediaItem = MediaItem.fromUri(mediaUri)

        // Create a base MediaSource
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        // Wrap in ClippingMediaSource with the loop start and end positions
        val preLoop = mediaSource.clip(0, loopStart)
        val loop = mediaSource.clip(loopStart, loopEnd)
        val postLoop = mediaSource.clip(loopEnd)

        setMediaItem(mediaItem)
        exoplayer.addMediaSource(preLoop)
        exoplayer.addMediaSource(loop)
        exoplayer.addMediaSource(postLoop)
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