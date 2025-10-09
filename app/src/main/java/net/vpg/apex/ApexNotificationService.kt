package net.vpg.apex

import android.app.NotificationManager
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class ApexNotificationService : MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        PlayerNotificationManager.Builder(this, 1001, "apex_playback")
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setSmallIconResourceId(R.drawable.ic_giratina_chill)
            .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
            .build()
            .also { it.setPlayer(mediaSession.player) }
            .also { it.setMediaSessionToken(mediaSession.platformToken) }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
}
