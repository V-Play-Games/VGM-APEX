package net.vpg.apex.player

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.util.UnstableApi
import net.vpg.apex.subBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

@UnstableApi
class LoopingAudioProcessor(
    _isLoopingEnabled: MutableState<Boolean>,
    _loopStartFrame: MutableIntState,
    _loopEndFrame: MutableIntState,
    val getMaxFrameLength: () -> Int
) : AudioProcessor {
    private var active = false
    private var ended = false

    private val loopStartFrame by _loopStartFrame
    private val loopEndFrame by _loopEndFrame
    private val isLoopingEnabled by _isLoopingEnabled

    private var bytesPerFrame = 0
    private var accumulator = EMPTY_BUFFER
    private var currentByteOffset = 0

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
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        ensureCapacity(inputBuffer.remaining())
        accumulator.put(inputBuffer)
    }

    override fun getOutput(): ByteBuffer {
        val limit = accumulator.position().let { availableBytes ->
            if (isLoopingEnabled) {
                min(loopEndFrame * bytesPerFrame, availableBytes)
            } else {
                availableBytes
            }
        }

        // pull out [currentByteOffset..limit)
        return accumulator.subBuffer(currentByteOffset * bytesPerFrame, limit).also {
            currentByteOffset = limit / bytesPerFrame
            if (currentByteOffset >= loopEndFrame) {
                if (isLoopingEnabled) {
                    currentByteOffset = loopStartFrame
                } else {
                    ended = true
                }
            }
        }
    }

    override fun flush() {
        println("LoopingAudioProcessor: flush() called")
        ended = false
        accumulator = EMPTY_BUFFER
        currentByteOffset = 0
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

        val newCap = maxOf(getMaxFrameLength() * bytesPerFrame, required)
        println("Resized accumulator from ${accumulator.capacity()} to $newCap bytes")

        accumulator = ByteBuffer
            .allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())
            .put(accumulator.flip() as ByteBuffer)
    }
}