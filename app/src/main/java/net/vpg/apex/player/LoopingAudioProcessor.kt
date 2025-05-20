package net.vpg.apex.player

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

@UnstableApi
class LoopingAudioProcessor(
    _isLoopingEnabled: MutableState<Boolean>,
    _loopStartFrame: MutableIntState,
    _loopEndFrame: MutableIntState
) : AudioProcessor {
    private var buffer = EMPTY_BUFFER
    private var outputBuffer = EMPTY_BUFFER
    private var active = false
    private var ended = false

    // Audio format details
    private var sampleRateHz = 0
    private var channelCount = 0
    private var encoding = C.ENCODING_INVALID
    private var bytesPerFrame = 0

    private var loopStartFrame by _loopStartFrame
    private var loopEndFrame by _loopEndFrame
    private var isLoopingEnabled by _isLoopingEnabled
    private var currentFramePosition = 0

    companion object {
        private val EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        sampleRateHz = inputAudioFormat.sampleRate
        channelCount = inputAudioFormat.channelCount
        encoding = inputAudioFormat.encoding

        bytesPerFrame = channelCount * (if (encoding == C.ENCODING_PCM_16BIT) 2 else 4)

        active = bytesPerFrame > 0
        return inputAudioFormat
    }

    override fun isActive(): Boolean = active

    override fun isEnded(): Boolean = ended

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
//        println("LoopingAudioProcessor: queueInput() called with ${inputBuffer.remaining()} bytes")
        if (!inputBuffer.hasRemaining()) {
            return
        }
        if (!active) {
            // Pass through if not active
            buffer = inputBuffer
            return
        }

        // How many frames are in this buffer
        val inputFrames = inputBuffer.remaining() / bytesPerFrame
        
        // Create a new buffer to hold our output data
        outputBuffer = ByteBuffer.allocateDirect(inputBuffer.remaining()).order(ByteOrder.nativeOrder())

        if (!isLoopingEnabled) {
            // If looping is disabled, just copy the input
            outputBuffer.put(inputBuffer)
            outputBuffer.flip()
            buffer = outputBuffer
            currentFramePosition += inputFrames
            return
        }

        // Process frame by frame
        val startPosition = inputBuffer.position()
        for (frameIndex in 0 until inputFrames) {
            // Check if we need to loop
            if (currentFramePosition >= loopEndFrame) {
                // We've reached the loop end, jump back to loop start
                currentFramePosition = loopStartFrame

                // Clear remaining input - we're jumping to a different point
                inputBuffer.position(inputBuffer.limit())
                break
            }

            // Copy this frame to output
            val frameStartPos = startPosition + (frameIndex * bytesPerFrame)
            for (b in 0 until bytesPerFrame) {
                outputBuffer.put(inputBuffer.get(frameStartPos + b))
            }

            // Advance the frame counter
            currentFramePosition++
        }

        // Update input buffer position
        inputBuffer.position(inputBuffer.limit())

        // Prepare the output buffer
        outputBuffer.flip()
        buffer = outputBuffer
    }

    override fun getOutput(): ByteBuffer {
//        println("LoopingAudioProcessor: getOutput() returned ${buffer.remaining()} bytes")
        val output = buffer
        buffer = EMPTY_BUFFER
        return output.slice()
    }

    override fun flush() {
        println("LoopingAudioProcessor: flush() called")
        buffer = EMPTY_BUFFER
        outputBuffer = EMPTY_BUFFER
        ended = false
    }

    override fun reset() {
        println("LoopingAudioProcessor: reset() called")
        flush()
        sampleRateHz = 0
        channelCount = 0
        encoding = C.ENCODING_INVALID
        bytesPerFrame = 0
        active = false
        currentFramePosition = 0
        ended = false
    }
}