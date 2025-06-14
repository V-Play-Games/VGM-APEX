package net.vpg.apex.core.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import net.vpg.apex.core.NotificationMaker

class MediaNotificationService : Service() {
    lateinit var notificationManager: NotificationManager
    private val notificationMaker = NotificationMaker()

    companion object {
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Initialize player and session
        startForeground(NOTIFICATION_ID, notificationMaker.createNotification(this))
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val channelId = "media_playback_channel"
        val channel = NotificationChannel(
            channelId,
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Used for media playback controls"
        }
        notificationManager.createNotificationChannel(channel)
    }
}