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
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import net.vpg.apex.ApexDownloadService
import net.vpg.apex.entities.ApexTrack

/** Tracks media that has been downloaded.  */
@OptIn(UnstableApi::class)
class DownloadTracker(downloadManager: DownloadManager) {
    /** Listens for changes in the tracked downloads.  */
    interface Listener {
        /** Called when the tracked downloads changed.  */
        fun onDownloadsChanged()
    }

    private val listeners = mutableListOf<Listener>()
    private val downloads = mutableMapOf<String, Download>()

    init {
        downloadManager.addListener(DownloadManagerListener())
        downloadManager.downloadIndex.getDownloads().use { loadedDownloads ->
            while (loadedDownloads.moveToNext()) {
                val download = loadedDownloads.download
                downloads.put(download.request.uri.toString(), download)
            }
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun isDownloaded(apexTrack: ApexTrack): Boolean {
        val download = downloads[apexTrack.url]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun toggleDownload(context: Context, apexTrack: ApexTrack) {
        val download = downloads[apexTrack.url]
        if (download != null && download.state != Download.STATE_FAILED) {
            DownloadService.sendRemoveDownload(
                context, ApexDownloadService::class.java, download.request.id, false
            )
        }
    }

    private inner class DownloadManagerListener : DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            downloads.put(download.request.uri.toString(), download)
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            downloads.remove(download.request.uri.toString())
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }
    }
}