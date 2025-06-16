/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.apex.util

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.*
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import net.vpg.apex.ApexDownloadService
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.*
import androidx.media3.exoplayer.offline.DownloadHelper.LiveContentUnsupportedException
import com.google.common.base.Preconditions
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet

/** Tracks media that has been downloaded.  */
@OptIn(UnstableApi::class)
class DownloadTracker(
    context: Context,
    private val dataSourceFactory: DataSource.Factory,
    downloadManager: DownloadManager
) {
    /** Listens for changes in the tracked downloads.  */
    interface Listener {
        /** Called when the tracked downloads changed.  */
        fun onDownloadsChanged()
    }

    private val context = context.applicationContext
    private val listeners = CopyOnWriteArraySet<Listener>()
    private val downloads = mutableMapOf<Uri?, Download?>()
    private val downloadIndex = downloadManager.downloadIndex

    private var startDownloadDialogHelper: StartDownloadDialogHelper? = null

    init {
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    fun addListener(listener: Listener?) {
        listeners.add(Preconditions.checkNotNull<Listener?>(listener))
    }

    fun removeListener(listener: Listener?) {
        listeners.remove(listener)
    }

    fun isDownloaded(mediaItem: MediaItem): Boolean {
        val download = downloads[mediaItem.localConfiguration?.uri]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun getDownloadRequest(uri: Uri?): DownloadRequest? {
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request else null
    }

    fun toggleDownload(
        mediaItem: MediaItem, renderersFactory: RenderersFactory?
    ) {
        val download = downloads.get(mediaItem.localConfiguration?.uri)
        if (download != null && download.state != Download.STATE_FAILED) {
            DownloadService.sendRemoveDownload(
                context, ApexDownloadService::class.java, download.request.id,  /* foreground= */false
            )
        } else {
            if (startDownloadDialogHelper != null) {
                startDownloadDialogHelper!!.release()
            }
            startDownloadDialogHelper =
                StartDownloadDialogHelper(
                    DownloadHelper.forMediaItem(context, mediaItem, renderersFactory, dataSourceFactory),
                    mediaItem
                )
        }
    }

    private fun loadDownloads() {
        try {
            downloadIndex.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    val download = loadedDownloads.download
                    downloads.put(download.request.uri, download)
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to query downloads", e)
        }
    }

    private inner class DownloadManagerListener : DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager, download: Download, finalException: Exception?
        ) {
            downloads.put(download.request.uri, download)
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            downloads.remove(download.request.uri)
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }
    }

    private inner class StartDownloadDialogHelper(
        private val downloadHelper: DownloadHelper,
        private val mediaItem: MediaItem
    ) : DownloadHelper.Callback, DialogInterface.OnDismissListener {
        private var keySetId: ByteArray? = null

        init {
            downloadHelper.prepare(this)
        }

        fun release() {
            downloadHelper.release()
        }

        // DownloadHelper.Callback implementation.
        override fun onPrepared(helper: DownloadHelper) {
            onDownloadPrepared()
        }

        override fun onPrepareError(helper: DownloadHelper, e: IOException) {
            val isLiveContent = e is LiveContentUnsupportedException
            val toastStringId =
                if (isLiveContent) "download_live_unsupported" else "download_start_error"
            val logMessage =
                if (isLiveContent) "Downloading live content unsupported" else "Failed to start download"
            Toast.makeText(context, toastStringId, Toast.LENGTH_LONG).show()
            Log.e(TAG, logMessage, e)
        }

        // DialogInterface.OnDismissListener implementation.
        override fun onDismiss(dialogInterface: DialogInterface?) {
            downloadHelper.release()
        }

        // Internal methods.

        fun onDownloadPrepared() {
            startDownload()
            downloadHelper.release()
            return
        }

        fun startDownload(downloadRequest: DownloadRequest = buildDownloadRequest()) {
            DownloadService.sendAddDownload(
                context, ApexDownloadService::class.java, downloadRequest,  /* foreground= */false
            )
        }

        fun buildDownloadRequest(): DownloadRequest {
            return downloadHelper
                .getDownloadRequest(
                    Util.getUtf8Bytes(mediaItem.mediaMetadata.title.toString())
                )
                .copyWithKeySetId(keySetId)
        }
    }

    companion object {
        private const val TAG = "DownloadTracker"
    }
}