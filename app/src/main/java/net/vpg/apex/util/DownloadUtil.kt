package net.vpg.apex.util


import android.content.Context
import android.net.http.HttpEngine
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpEngineDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import net.vpg.apex.core.di.rememberDatabaseProvider
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.Executors

/** Utility methods for the demo app.  */
object DownloadUtil {
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID: String = "download_channel"

    private const val TAG = "DemoUtil"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    private var httpDataSourceFactory: DataSource.Factory? = null

    private var downloadDirectory: File? = null

    @OptIn(UnstableApi::class)
    private var downloadCache: Cache? = null

    @OptIn(UnstableApi::class)
    private var downloadManager: DownloadManager? = null

    private var downloadTracker: DownloadTracker? = null

    @OptIn(UnstableApi::class)
    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    @OptIn(UnstableApi::class)
    @Synchronized
    fun getHttpDataSourceFactory(context: Context): DataSource.Factory {
        var context = context
        if (httpDataSourceFactory != null) {
            return httpDataSourceFactory!!
        }
        context = context.applicationContext
        if (Build.VERSION.SDK_INT >= 30
            && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 7
        ) {
            val httpEngine = HttpEngine.Builder(context).build()
            httpDataSourceFactory =
                HttpEngineDataSource.Factory(httpEngine, Executors.newSingleThreadExecutor())
            return httpDataSourceFactory!!
        }
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        CookieHandler.setDefault(cookieManager)
        httpDataSourceFactory = DefaultHttpDataSource.Factory()
        return httpDataSourceFactory!!
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper {
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

    @Synchronized
    fun getDownloadTracker(context: Context): DownloadTracker? {
        ensureDownloadManagerInitialized(context)
        return downloadTracker
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    private fun getDownloadCache(context: Context): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory =
                File(context.filesDir, DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(
                downloadContentDirectory,
                NoOpCacheEvictor(),
                rememberDatabaseProvider(context)
            )
        }
        return downloadCache!!
    }

    @OptIn(UnstableApi::class)
    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if (downloadManager == null) {
            downloadManager =
                DownloadManager(
                    context,
                    rememberDatabaseProvider(context),
                    getDownloadCache(context),
                    getHttpDataSourceFactory(context),
                    Executors.newFixedThreadPool(6)
                )
            downloadTracker =
                DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager!!)
        }
    }
}