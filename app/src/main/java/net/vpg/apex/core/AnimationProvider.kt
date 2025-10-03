package net.vpg.apex.core

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import net.vpg.apex.core.di.rememberSettings

class AnimationProvider(val speedMultiplier: Float) {
    val fastDuration = (150 / speedMultiplier).toInt()
    val mediumDuration = (300 / speedMultiplier).toInt()
    val slowDuration = (500 / speedMultiplier).toInt()

    fun <T> fastSpec(): AnimationSpec<T> = tween(fastDuration)
    fun <T> mediumSpec(): AnimationSpec<T> = tween(mediumDuration)
    fun <T> slowSpec(): AnimationSpec<T> = tween(slowDuration)
}

@Composable
fun rememberAnimationProvider(): AnimationProvider {
    val settings = rememberSettings()
    val animationSpeed = settings.animationSpeed.collectAsState(1.0f).value

    return remember(animationSpeed) {
        AnimationProvider(speedMultiplier = animationSpeed)
    }
}
