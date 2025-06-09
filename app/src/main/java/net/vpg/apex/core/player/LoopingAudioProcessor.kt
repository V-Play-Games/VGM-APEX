package net.vpg.apex.core.player

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.AudioProcessor.EMPTY_BUFFER
import androidx.media3.common.util.UnstableApi
import net.vpg.apex.core.savePcmAsWav
import net.vpg.apex.core.subBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.logging.Logger
import kotlin.math.max
import kotlin.math.min

@UnstableApi
internal class LoopingAudioProcessor(val player: ApexPlayer) : AudioProcessor {
    private var active = false
    private var ended = false

    private var format = AudioFormat.NOT_SET
    private var audioData = EMPTY_BUFFER
    private var currentFrame = 0
    internal val currentPositionMs get() = currentFrame.toLong() * 1000 / format.sampleRate
    private var recentlySeekedFrame = -1

    companion object {
        private val LOGGER = Logger.getLogger(LoopingAudioProcessor::class.java.name)
    }

    override fun configure(format: AudioFormat) = format.also {
        this.format = format
        active = true
    }

    override fun isActive() = active

    override fun isEnded() = ended

    override fun queueEndOfStream() {}

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        ensureCapacity(inputBuffer.remaining())
        audioData.put(inputBuffer)
        println("Accumulated ${inputBuffer.position()} bytes of audio data, current position: ${audioData.position()}")
        if (audioData.position() >= player.nowPlaying.frameLength * format.bytesPerFrame) {
            player.nowPlaying.downloadedFile(player.cacheDir)
                .takeIf { !it.exists() }
                ?.let { player.nowPlaying.cacheFile(player.cacheDir) }
                ?.takeIf { !it.exists() }
                ?.also { it.createNewFile() }
                ?.let { cacheFile -> audioData.array().savePcmAsWav(cacheFile, format) }
                ?.also { LOGGER.info("Cache audio data for ${player.nowPlaying.id}") }
        }
    }

    override fun getOutput(): ByteBuffer {
        if (recentlySeekedFrame >= 0) {
            LOGGER.info("Seeking to frame $recentlySeekedFrame")
            currentFrame = recentlySeekedFrame
            recentlySeekedFrame = -1
        }
        val startPos = currentFrame * format.bytesPerFrame
        val maxSafeLength = startPos + format.bytesPerFrame * format.sampleRate / 20
        val absoluteEndPos = min(
            if (player.isLooping && currentFrame <= player.loopEnd)
                player.loopEnd * format.bytesPerFrame
            else
                Int.MAX_VALUE,
            max(audioData.capacity(), player.nowPlaying.frameLength * format.bytesPerFrame)
        )
        val limit = minOf(absoluteEndPos, audioData.position(), maxSafeLength)
        currentFrame = limit / format.bytesPerFrame
        ended = currentFrame * format.bytesPerFrame >= absoluteEndPos
        if (player.isLooping && ended) {
            ended = false
            currentFrame = if (currentFrame == player.loopEnd) player.loopStart else 0
        }
        return audioData.subBuffer(startPos, limit)
    }

    override fun flush() {
        LOGGER.info("flush() called")
        ended = false
        audioData = EMPTY_BUFFER
        currentFrame = 0
    }

    override fun reset() {
        LOGGER.info("reset() called")
        flush()
        format = AudioFormat.NOT_SET
        active = false
    }

    fun seekTo(positionMs: Long) {
        recentlySeekedFrame = (positionMs / 1000 * format.sampleRate).toInt()
    }

    private fun ensureCapacity(additional: Int) {
        val required = audioData.position() + additional
        if (audioData.capacity() >= required) return

        val newCap = maxOf(player.nowPlaying.frameLength * format.bytesPerFrame, required)
        LOGGER.info("Resized accumulator from ${audioData.capacity()} to $newCap bytes")

        audioData = ByteBuffer.allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())
            .put(audioData.flip() as ByteBuffer)
    }
}
