package net.vpg.apex

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.DefaultMediaDescriptionAdapter
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.core.player.CoilBitmapLoader
import net.vpg.apex.ui.screens.NowPlayingScreen

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class ApexNotificationService : MediaSessionService() {
    companion object {
        const val NOW_PLAYING = "net.vpg.apex.ACTION_NAVIGATE"
    }

    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        val notificationChannelId = "apex_playback"
        val notificationId = 1001

        val intent = Intent(this, ApexNotificationService::class.java).apply {
            action = NOW_PLAYING
        }
        val pendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val player = rememberPlayer(this)
        mediaSession = MediaSession.Builder(this, player)
            .setBitmapLoader(CoilBitmapLoader(this))
            .build()
        PlayerNotificationManager.Builder(this, notificationId, notificationChannelId)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setSmallIconResourceId(R.drawable.ic_pika_chill)
            .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
            .setMediaDescriptionAdapter(DefaultMediaDescriptionAdapter(pendingIntent))
            .build()
            .also { it.setPlayer(player) }
            .also { it.setMediaSessionToken(mediaSession.platformToken) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) =
        super.onStartCommand(intent, flags, startId).also {
            if (intent?.action == NOW_PLAYING) {
                NowPlayingScreen.navigate()
            }
        }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
}
