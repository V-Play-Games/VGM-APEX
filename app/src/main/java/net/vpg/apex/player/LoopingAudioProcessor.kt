package net.vpg.apex.player

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.util.UnstableApi
import net.vpg.apex.savePcmAsWav
import net.vpg.apex.subBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.logging.Logger
import kotlin.math.min

@UnstableApi
class LoopingAudioProcessor(val player: ApexPlayer) : AudioProcessor {
    private var active = false
    private var ended = false

    private var format = AudioFormat.NOT_SET
    private var audioData = EMPTY_BUFFER
    private var currentFrame = 0

    companion object {
        private val EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
        private val logger = Logger.getLogger(LoopingAudioProcessor::class.java.name)
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
        if (audioData.position() >= player.nowPlaying.frameLength * format.bytesPerFrame) {
            player.nowPlaying.downloadedFile(player.cacheDir)
                .takeIf { !it.exists() }
                ?.let { player.nowPlaying.cacheFile(player.cacheDir) }
                ?.takeIf { !it.exists() }
                ?.also { it.createNewFile() }
                ?.let { cacheFile -> audioData.array().savePcmAsWav(cacheFile, format) }
                ?.also { logger.info("Cache audio data for ${player.nowPlaying.id}") }
        }
    }

    override fun getOutput(): ByteBuffer {
        val startPos = currentFrame * format.bytesPerFrame
        val limit = audioData.position().let { availableBytes ->
            if (player.isLooping) {
                min(player.loopEnd * format.bytesPerFrame, availableBytes)
            } else {
                availableBytes
            }
        }
        currentFrame = limit / format.bytesPerFrame
        ended = currentFrame == player.loopEnd && !player.isLooping
        if (player.isLooping && currentFrame == player.loopEnd) {
            currentFrame = player.loopStart
        }

        return audioData.subBuffer(startPos, limit)
    }

    override fun flush() {
        logger.info("flush() called")
        ended = false
        audioData = EMPTY_BUFFER
        currentFrame = 0
    }

    override fun reset() {
        logger.info("reset() called")
        flush()
        format = AudioFormat.NOT_SET
        active = false
    }

    private fun ensureCapacity(additional: Int) {
        val required = audioData.position() + additional
        if (audioData.capacity() >= required) return

        val newCap = maxOf(player.nowPlaying.frameLength * format.bytesPerFrame, required)
        logger.info("Resized accumulator from ${audioData.capacity()} to $newCap bytes")

        audioData = ByteBuffer.allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())
            .put(audioData.flip() as ByteBuffer)
    }
}
