package net.vpg.apex.core

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.annotation.FloatRange
import androidx.annotation.OptIn
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.valentinilk.shimmer.*
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.R
import net.vpg.apex.core.player.ApexPlayer
import net.vpg.apex.core.player.MediaControlReceiver.Companion.ACTION_NEXT
import net.vpg.apex.core.player.MediaControlReceiver.Companion.ACTION_PLAY_PAUSE
import net.vpg.apex.core.player.MediaControlReceiver.Companion.ACTION_PREVIOUS
import javax.inject.Inject

fun Context.unwrapActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.unwrapActivity()
    else -> throw IllegalStateException("Not an activity")
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

fun Modifier.bounceClick(
    @FloatRange(0.0, 1.0) scaleModifier: Float = 0.95f,
    @FloatRange(0.0, 1.0) alphaModifier: Float = 0.8f,
    onClick: () -> Unit
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) scaleModifier else 1f)
    val alpha by animateFloatAsState(if (isPressed) alphaModifier else 1f)

    this
        .scale(scale)
        .alpha(alpha)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onClick(); isPressed = false }
        )
        .pointerInput(isPressed) {
            awaitPointerEventScope {
                if (isPressed) {
                    waitForUpOrCancellation()
                } else {
                    awaitFirstDown()
                }
                isPressed = !isPressed
            }
        }
}

@Composable
fun Modifier.customShimmer(
    durationMillis: Int = DefaultDurationMillis,
    delayMillis: Int = 0,
    easing: Easing = LinearEasing,
) = this.shimmer(
    rememberShimmer(
        shimmerBounds = ShimmerBounds.View,
        theme = LocalShimmerTheme.current.copy(
            animationSpec = infiniteRepeatable(
                animation = shimmerSpec(
                    durationMillis = durationMillis,
                    delayMillis = delayMillis,
                    easing = easing
                )
            )
        )
    )
)

@AndroidEntryPoint
class NotificationMaker() {
    @Inject
    lateinit var player: ApexPlayer
    @Inject
    lateinit var mediaSession: MediaSession

    @OptIn(UnstableApi::class)
    fun createNotification(context: Context): Notification {
        val currentTrack = player.nowPlaying

        // Create intents for media actions
        val playPauseIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_PLAY_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            context, 1,
            Intent(ACTION_PREVIOUS),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            context, 2,
            Intent(ACTION_NEXT),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = Notification.Builder(context, "media_playback_channel")
            .setSmallIcon(net.vpg.apex.R.drawable.ic_pika_chill)
            .setContentTitle(currentTrack.title)
            .setContentText(currentTrack.uploader.name)
            .setOngoing(true)
            .setVisibility(Notification.VISIBILITY_PUBLIC)

        // Add media actions
        val icon = Icon.createWithResource(context, R.drawable.ic_pika_chill)
        builder.addAction(
            Notification.Action.Builder(
                icon,
                "Previous",
                prevIntent
            ).build()
        )

//        val playPauseIcon = if (player.isPlaying)
//            R.drawable.ic_pause else R.drawable.ic_play
        builder.addAction(
            Notification.Action.Builder(
                icon,
                "Play/Pause", playPauseIntent
            ).build()
        )

        builder.addAction(
            Notification.Action.Builder(
                icon,
                "Next", nextIntent
            ).build()
        )

        // Apply MediaStyle
        builder.setStyle(
            Notification.MediaStyle()
                .setMediaSession(mediaSession.platformToken)
                .setShowActionsInCompactView(0, 1, 2)
        )

        return builder.build()
    }

}