package net.vpg.apex.util


import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import net.vpg.apex.core.di.rememberDatabaseProvider
import java.io.File
import java.util.concurrent.Executors

/** Utility methods for the demo app.  */
object DownloadUtil {
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID: String = "download_channel"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    @OptIn(UnstableApi::class)
    private var downloadManager: DownloadManager? = null

    @OptIn(UnstableApi::class)
    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    @OptIn(UnstableApi::class)
    @Synchronized
    fun getDownloadNotificationHelper(context: Context): DownloadNotificationHelper {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper =
                DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        }
        return downloadNotificationHelper!!
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager {
        ensureDownloadManagerInitialized(context)
        return downloadManager!!
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if (downloadManager == null) {
            downloadManager = DownloadManager(
                context,
                rememberDatabaseProvider(context),
                SimpleCache(
                    File(context.filesDir, DOWNLOAD_CONTENT_DIRECTORY),
                    NoOpCacheEvictor(),
                    rememberDatabaseProvider(context)
                ),
                DefaultDataSource.Factory(context),
                Executors.newFixedThreadPool(6)
            )
        }
    }
}