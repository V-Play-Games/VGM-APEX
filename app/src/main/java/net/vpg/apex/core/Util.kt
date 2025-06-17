package net.vpg.apex.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.FloatRange
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
import com.valentinilk.shimmer.*

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
    condition: Boolean = true,
    durationMillis: Int = DefaultDurationMillis,
    delayMillis: Int = 0,
    easing: Easing = LinearEasing,
) = this.takeIf { condition }?.shimmer(
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
) ?: this
