package net.vpg.apex.core.player

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(UnstableApi::class)
class CoilBitmapLoader(val context: Context) : BitmapLoader {
    private val imageLoader = ImageLoader.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val tag = "CoilBitmapLoader"

    override fun supportsMimeType(mimeType: String): Boolean {
        // Coil supports common image types; customize as needed
        return mimeType.startsWith("image/")
    }

    override fun decodeBitmap(data: ByteArray) = loadImageAsBitmap(data)

    override fun loadBitmap(uri: Uri) = loadImageAsBitmap(uri)

    private fun loadImageAsBitmap(data: Any?): ListenableFuture<Bitmap> {
        return CallbackToFutureAdapter.getFuture { completer ->
            scope.launch {
                ImageRequest.Builder(context)
                    .data(data)
                    .target(
                        onStart = { Log.d(tag, "Started loading image: $it") },
                        onSuccess = {
                            Log.d(tag, "Successfully loaded image: $it")
                            completer.set(it.toBitmap())
                        },
                        onError = {
                            Log.e(tag, "Error while loading image: $it")
                            completer.setException(IOException("Failed to load image"))
                        }
                    )
                    .build()
                    .also { imageLoader.enqueue(it) }
            }
        }
    }
}