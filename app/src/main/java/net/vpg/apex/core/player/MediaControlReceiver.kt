package net.vpg.apex.core.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaControlReceiver : BroadcastReceiver() {
    @Inject lateinit var player: ApexPlayer

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                player.togglePlayPause()
            }
            ACTION_PREVIOUS -> {
                player.previousTrack()
            }
            ACTION_NEXT -> {
                player.nextTrack()
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "net.vpg.apex.ACTION_PLAY_PAUSE"
        const val ACTION_PREVIOUS = "net.vpg.apex.ACTION_PREVIOUS"
        const val ACTION_NEXT = "net.vpg.apex.ACTION_NEXT"
    }
}