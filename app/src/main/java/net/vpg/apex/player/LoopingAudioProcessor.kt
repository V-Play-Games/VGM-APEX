package net.vpg.apex.player

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    private val loopStartFrame by _loopStartFrame
    private val loopEndFrame by _loopEndFrame
    private val isLoopingEnabled by _isLoopingEnabled
    private var currentFramePosition = 0
    private var accumulator: ByteBuffer = EMPTY_BUFFER

    companion object {
        private val EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    override fun configure(format: AudioProcessor.AudioFormat) = format.also {
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
        ensureCapacity(inputBuffer.remaining())
        accumulator.put(inputBuffer)
    }

    override fun getOutput(): ByteBuffer {
        val limit = if (isLoopingEnabled) loopEndFrame else accumulator.position()
        val len = minOf(limit - currentFramePosition, sampleRateHz / 20)
        return accumulator.subBuffer(currentFramePosition, currentFramePosition + len).also {
            currentFramePosition += len
            if (currentFramePosition == loopEndFrame && isLoopingEnabled) {
                currentFramePosition = loopStartFrame
            }
        }
        //    private fun playAudio() {
        //        while (isPlaying) {
        //            val limit = if (loopCount == 0) data.frameLength else loopEnd
        //            val len = min(limit - data.readPos, frameRate / 20)
        //            data.readData(len).also { sourceDataLine.write(it, 0, it.size) }
        //            if (data.readPos == loopEnd && loopCount != 0) {
        //                data.readPos = loopStart
        //                if (loopCount != Clip.LOOP_CONTINUOUSLY) loopCount--
        //            }
        //        }
        //    }
    }

    fun ByteBuffer.subBuffer(start: Int, end: Int): ByteBuffer {
        require(start in 0..capacity()) { "start($start) must be within [0, capacity=${capacity()}]" }
        require(end in start..capacity()) { "end($end) must be within [start=$start, capacity=${capacity()}]" }
        // Duplicate so we don’t disturb the original buffer’s position/limit.
        val dup = this.duplicate()
        // Set up the window:
        dup.position(start)
        dup.limit(end)
        // slice() creates a new buffer from position..limit
        return dup.slice()
    }


    /** Returns a read‐only slice of all raw data ever queued. */
    fun getAllBufferedData(): ByteBuffer {
        val slice = accumulator.duplicate()
        slice.flip()
        return slice.asReadOnlyBuffer()
    }

    override fun flush() {
        println("LoopingAudioProcessor: flush() called")
        ended = false
        accumulator.clear()    // drop all accumulated data
    }

    override fun reset() {
        println("LoopingAudioProcessor: reset() called")
        flush()
        sampleRateHz = 0
        channelCount = 0
        encoding = C.ENCODING_INVALID
        active = false
        currentFramePosition = 0
        ended = false
    }

    /** Grow `accumulator` if needed to fit `additional` more bytes. */
    private fun ensureCapacity(additional: Int) {
        val required = accumulator.position() + additional
        if (accumulator.capacity() >= required) return

        // new capacity: double or just enough to cover required
        val newCap = maxOf(getMaxFrameLength(), required)
        val bigger = ByteBuffer
            .allocateDirect(newCap)
            .order(ByteOrder.nativeOrder())

        // copy old data
        accumulator.flip()
        bigger.put(accumulator)

        accumulator = bigger
    }
}