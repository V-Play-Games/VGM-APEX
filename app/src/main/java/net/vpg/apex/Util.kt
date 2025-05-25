package net.vpg.apex

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.core.net.toUri
import java.io.ByteArrayInputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
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

fun ByteArray.toUri() = URL(
    null,
    "bytes:///" + "audio",
    object : URLStreamHandler() {
        override fun openConnection(u: URL) = object : URLConnection(u) {
            override fun connect() {}
            override fun getInputStream() = ByteArrayInputStream(this@toUri)
        }
    }
).toURI().toString().toUri()
