package net.vpg.apex.player

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.util.UnstableApi
import net.vpg.apex.subBuffer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class LoopingAudioProcessor(val player: ApexPlayer) : AudioProcessor {
    private var active = false
    private var ended = false

    private var bytesPerFrame = 0
    private var accumulator = EMPTY_BUFFER
    private var currentFrame = 0

    companion object {
        private val EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    override fun configure(format: AudioFormat) = format.also {
        bytesPerFrame = format.bytesPerFrame
        active = true
    }

    override fun isActive() = active

    override fun isEnded() = ended

    override fun queueEndOfStream() {
        println("LoopingAudioProcessor: queueEndOfStream() called")
        File(player.cacheDir, player.nowPlaying.id + ".cache").writeBytes(accumulator.array())
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        ensureCapacity(inputBuffer.remaining())
        accumulator.put(inputBuffer)
    }

    override fun getOutput(): ByteBuffer {
        val startPos = currentFrame * bytesPerFrame
        val limit = accumulator.position().let { availableBytes ->
            if (player.isLooping) {
                min(player.loopEnd * bytesPerFrame, availableBytes)
            } else {
                availableBytes
            }
        }
        currentFrame = limit / bytesPerFrame
        ended = currentFrame == player.loopEnd && !player.isLooping
        if (player.isLooping && currentFrame == player.loopEnd) {
            currentFrame = max(0, player.loopStart)
        }

        return accumulator.subBuffer(startPos, limit)
    }

    override fun flush() {
        println("LoopingAudioProcessor: flush() called")
        ended = false
        accumulator = EMPTY_BUFFER
        currentFrame = 0
    }

    override fun reset() {
        println("LoopingAudioProcessor: reset() called")
        flush()
        bytesPerFrame = 0
        active = false
    }

    private fun ensureCapacity(additional: Int) {
        val required = accumulator.position() + additional
        if (accumulator.capacity() >= required) return

        val newCap = maxOf(player.nowPlaying.frameLength * bytesPerFrame, required)
        println("Resized accumulator from ${accumulator.capacity()} to $newCap bytes")

        accumulator = ByteBuffer.allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())
            .put(accumulator.flip() as ByteBuffer)
    }
}