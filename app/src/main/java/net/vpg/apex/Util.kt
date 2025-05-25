package net.vpg.apex

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.nio.ByteBuffer

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
