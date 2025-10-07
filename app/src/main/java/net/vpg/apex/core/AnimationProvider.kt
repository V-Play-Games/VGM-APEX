package net.vpg.apex.core

import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import net.vpg.apex.core.di.rememberSettings

class AnimationProvider(val speedMultiplier: Float) {
    private val fastDuration get() = (150 / speedMultiplier).toInt()
    private val mediumDuration get() = (300 / speedMultiplier).toInt()
    private val slowDuration get() = (500 / speedMultiplier).toInt()

    fun <T> fastSpec() = tween<T>(fastDuration)
    fun <T> mediumSpec() = tween<T>(mediumDuration)
    fun <T> slowSpec() = tween<T>(slowDuration)
}

@Composable
fun rememberAnimationProvider() =
    rememberSettings().animationSpeed.collectAsState(1.0f).value.let { animationSpeed ->
        remember(animationSpeed) { AnimationProvider(animationSpeed) }
    }
