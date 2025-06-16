package net.vpg.apex.util

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService.sendAddDownload
import kotlinx.coroutines.*
import net.vpg.apex.ApexDownloadService
import net.vpg.apex.core.DownloadState
import net.vpg.apex.entities.ApexTrack

@OptIn(UnstableApi::class)
class DownloadTracker(val context: Context, downloadManager: DownloadManager) {
    private val downloads = mutableMapOf<String, DownloadState>()
    private val ongoingDownloads = mutableMapOf<String, Download>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        downloadManager.addListener(object : DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                when (download.state) {
                    Download.STATE_COMPLETED, Download.STATE_FAILED -> {
                        ongoingDownloads.remove(download.request.id)
                    }

                    Download.STATE_DOWNLOADING -> {
                        ongoingDownloads[download.request.id] = download
                    }

                    else -> {}
                }
                println("Download changed: ${download.request.id} - ${download.state}, ${download.percentDownloaded}")
                getDownloadState(download.request.id).downloadState = download.state
            }
        })
        downloadManager.downloadIndex.getDownloads().use { loadedDownloads ->
            while (loadedDownloads.moveToNext()) {
                val download = loadedDownloads.download
                downloads[download.request.id] = DownloadState(
                    id = download.request.id,
                    initialDownloadState = download.state,
                    downloadTracker = this
                )
            }
        }
        scope.launch {
            while (true) {
                ongoingDownloads.forEach { (id, download) ->
                    downloads[id]?.progress = download.percentDownloaded
                }
                delay(50)
            }
        }
    }

    fun getDownloadState(id: String) = downloads.computeIfAbsent(id) {
        DownloadState(id, downloadTracker = this)
    }

    @OptIn(UnstableApi::class)
    fun download(apexTrack: ApexTrack) {
        val downloadRequest = DownloadRequest.Builder(apexTrack.id, apexTrack.url.toUri()).build()
        sendAddDownload(
            context,
            ApexDownloadService::class.java,
            downloadRequest,
            false
        )
    }
}