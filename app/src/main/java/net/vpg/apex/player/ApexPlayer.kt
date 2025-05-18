package net.vpg.apex.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class ApexPlayer {
    private val player: ExoPlayer
    private val _nowPlaying = mutableStateOf(ApexTrack.EMPTY)
    val nowPlaying: State<ApexTrack> = _nowPlaying
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying
    private val _isBuffering = mutableStateOf(false)
    val isBuffering: State<Boolean> = _isBuffering

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
                    }

                    Player.STATE_ENDED -> {
                    }

                    Player.STATE_IDLE -> {
                    }
                }
            }
        })
    }

    fun play(track: ApexTrack) {
        _nowPlaying.value = track
        player.setMediaItem(track.toMediaItem())
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying)
            player.pause()
        else
            player.play()
    }
}
