package net.vpg.apex.core

import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import net.vpg.apex.core.di.rememberDownloadTracker
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.util.DownloadTracker

@Composable
fun ApexTrack.rememberDownloadState() = rememberDownloadTracker().getDownloadState(id)

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
