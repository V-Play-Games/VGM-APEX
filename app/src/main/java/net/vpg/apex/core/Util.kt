package net.vpg.apex.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.annotation.OptIn
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
