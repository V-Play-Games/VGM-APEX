package net.vpg.apex.core

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.util.DownloadTracker

@OptIn(UnstableApi::class)
class DownloadState(
    val id: String,
    initialDownloadState: Int = -1,
    val downloadTracker: DownloadTracker
) {
    var downloadState by mutableIntStateOf(initialDownloadState)
    val isDownloaded
        get() = downloadState == Download.STATE_COMPLETED
    val isPending
        get() = downloadState == Download.STATE_QUEUED || downloadState == Download.STATE_RESTARTING
    val isDownloading
        get() = downloadState == Download.STATE_DOWNLOADING
    var progress by mutableFloatStateOf(if (isDownloaded) 1f else -1f)

    fun download() {
        downloadState = Download.STATE_QUEUED
        downloadTracker.download(ApexTrack.TRACKS_DB[id]!!)
    }
}
