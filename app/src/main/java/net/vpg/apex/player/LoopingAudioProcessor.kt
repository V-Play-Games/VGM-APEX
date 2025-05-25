package net.vpg.apex.player

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.util.UnstableApi
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

    // Audio format details
    private var sampleRateHz = 0
    private var channelCount = 0
    private var encoding = C.ENCODING_INVALID

    // your loop markers in FRAME‐units:
    private val loopStartFrame by _loopStartFrame
    private val loopEndFrame by _loopEndFrame

    // byte‐size of one PCM frame at this format
    private val bytesPerFrame
        get() = channelCount * when (encoding) {
            C.ENCODING_PCM_8BIT -> 1
            C.ENCODING_PCM_16BIT -> 2
            C.ENCODING_PCM_FLOAT -> 4
            else -> 2
        }

    // compute byte offset:
    private val loopStartByte get() = loopStartFrame * bytesPerFrame
    private val loopEndByte get() = loopEndFrame * bytesPerFrame

    private val isLoopingEnabled by _isLoopingEnabled
    private var currentFramePosition = 0
    private var accumulator: ByteBuffer = EMPTY_BUFFER
    private var currentByteOffset = 0
    private var buffer50ms: ByteBuffer = EMPTY_BUFFER
    private var bufferFullLoop: ByteBuffer = EMPTY_BUFFER
    val bytesFor50ms get() = (sampleRateHz * channelCount * bytesPerFrame * 0.05).toInt()

    companion object {
        private val EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    override fun configure(format: AudioFormat) = format.also {
        sampleRateHz = format.sampleRate
        channelCount = format.channelCount
        encoding = format.encoding
        active = true
    }

    override fun isActive() = active

    override fun isEnded() = ended

    override fun queueEndOfStream() {
        println("LoopingAudioProcessor: queueEndOfStream() called")
        // Only mark as ended if looping is disabled
        ended = !isLoopingEnabled

        // If we're looping, reset position to loop start
        if (isLoopingEnabled) {
            currentFramePosition = loopStartFrame
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        accumulator = accumulator.ensureCapacity("accumulator", inputBuffer.remaining())
        accumulator.put(inputBuffer)
    }

    override fun getOutput(): ByteBuffer {
        val limit = accumulator.position().let { availableBytes ->
            if (isLoopingEnabled) {
                min(loopEndByte, availableBytes)
            } else {
                availableBytes
            }
        }

        // pull out [currentByteOffset..limit)
        return subBuffer(accumulator, currentByteOffset, limit).also {
            currentByteOffset = limit
            if (currentByteOffset >= loopEndByte && isLoopingEnabled) {
                // wrap
                currentByteOffset = loopStartByte
            }
        }
    }

    fun subBuffer(buffer: ByteBuffer, start: Int, end: Int): ByteBuffer {
        if (start - end == 0) return EMPTY_BUFFER
        require(start in 0..buffer.capacity()) { "start($start) must be within [0, capacity=${buffer.capacity()}]" }
        require(end in start..buffer.capacity()) { "end($end) must be within [start=$start, capacity=${buffer.capacity()}]" }
        val length = end - start
        val arr = ByteArray(length)
        val oldPosition = buffer.position()
        buffer.position(start)
        buffer.get(arr, 0, length)
        buffer.position(oldPosition)
//        var output = if (length < bytesFor50ms) buffer50ms else bufferFullLoop
//        output.clear()
//        output = output.ensureCapacity(if (length < bytesFor50ms) "buffer50ms" else "bufferFullLoop", length)
//        output.put(arr)
//        return output
        return ByteBuffer.wrap(arr)
    }
//        return ByteArray(end - start)
//            .also { accumulator.get(it, start, it.size) }
//            .also { println(it.contentToString()) }
//            .let { ByteBuffer.wrap(it) }
//    }

    override fun flush() {
        println("LoopingAudioProcessor: flush() called")
        ended = false
        accumulator = EMPTY_BUFFER
        currentByteOffset = 0
    }

    override fun reset() {
        println("LoopingAudioProcessor: reset() called")
        flush()
        sampleRateHz = 0
        channelCount = 0
        encoding = C.ENCODING_INVALID
        active = false
        currentFramePosition = 0
    }

    /** Grow `accumulator` if needed to fit `additional` more bytes. */
    private fun ByteBuffer.ensureCapacity(bufferName: String, additional: Int): ByteBuffer {
        val required = position() + additional
        if (capacity() >= required) return this

        // new capacity: double or just enough to cover required
        val newCap = maxOf(getMaxFrameLength() * bytesPerFrame, required)
        val bigger = ByteBuffer
            .allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())

        // copy old data
        flip()
        bigger.put(this)
        println("Resized $bufferName from ${capacity()} to $newCap bytes")
        return bigger
    }
}