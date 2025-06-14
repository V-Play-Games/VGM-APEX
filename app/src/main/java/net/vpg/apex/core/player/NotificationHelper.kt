package net.vpg.apex.core.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import net.vpg.apex.R

class NotificationHelper(
    val context: Context,
    val player: ApexPlayer,
    val mediaSession: MediaSession
) {
    companion object {
        private const val NOTIFICATION_ID = "my_channel_id"
    }

    private val channel = NotificationChannel(
        NOTIFICATION_ID,
        "My Channel",
        NotificationManager.IMPORTANCE_LOW
    )
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        notificationManager.createNotificationChannel(channel)
    }

    @OptIn(UnstableApi::class)
    fun showNotification() {
        Notification.Builder(context, NOTIFICATION_ID)
            .setSmallIcon(R.drawable.ic_pika_chill)
            .setContentTitle(player.nowPlaying.title)
            .setContentText(player.nowPlaying.uploader.name)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession.platformToken))
            .build()
            .also { notification -> notificationManager.notify(1001, notification) }
    }
}