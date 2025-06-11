package net.vpg.apex.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.FloatRange
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun Context.unwrapActivity(): Activity = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.unwrapActivity()
    else -> throw IllegalStateException("Not an activity")
}

fun ByteBuffer.subBuffer(start: Int, end: Int): ByteBuffer {
    require(start in 0..capacity()) { "start($start) must be within [0, capacity=${capacity()}]" }
    require(end in start..capacity()) { "end($end) must be within [start=$start, capacity=${capacity()}]" }
    return ByteArray(end - start).also {
        val oldPosition = position()
        position(start)
        get(it, 0, it.size)
        position(oldPosition)
    }.let { ByteBuffer.wrap(it) }
}

@OptIn(UnstableApi::class)
fun ByteArray.savePcmAsWav(outputFile: File, format: AudioProcessor.AudioFormat) = this.savePcmAsWav(
    outputFile = outputFile,
    sampleRate = format.sampleRate,
    channels = format.channelCount,
    bytesPerSample = format.bytesPerFrame / format.channelCount
)

fun ByteArray.savePcmAsWav(outputFile: File, sampleRate: Int, channels: Int, bytesPerSample: Int) {
    val blockAlign = channels * bytesPerSample
    val byteRate = sampleRate * blockAlign
    val totalDataLen = this.size + 36
    val totalAudioLen = this.size

    val header = ByteBuffer.allocate(44)
        .order(ByteOrder.LITTLE_ENDIAN)
        .put("RIFF".toByteArray())
        .putInt(totalDataLen)
        .put("WAVE".toByteArray())
        .put("fmt ".toByteArray())
        .putInt(16) // Subchunk1Size for PCM
        .putShort(1) // AudioFormat (1 = PCM)
        .putShort(channels.toShort())
        .putInt(sampleRate)
        .putInt(byteRate)
        .putShort(blockAlign.toShort()) // BlockAlign
        .putShort((bytesPerSample * 8).toShort()) // Bits per sample
        .put("data".toByteArray())
        .putInt(totalAudioLen)

    outputFile.appendBytes(header.array())
    outputFile.appendBytes(this)
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
