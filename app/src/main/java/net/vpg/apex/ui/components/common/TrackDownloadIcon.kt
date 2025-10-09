package net.vpg.apex.ui.components.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.vpg.apex.util.bounceClick
import net.vpg.apex.util.rememberDownloadState
import net.vpg.apex.entities.ApexTrack

@Composable
fun TrackDownloadIcon(
    apexTrack: ApexTrack,
    size: Dp = 24.dp
) {
    val downloadState = apexTrack.rememberDownloadState()
    val progress by animateFloatAsState(downloadState.progress)

    if (downloadState.isDownloaded)
        Icon(
            imageVector = Icons.Outlined.DownloadDone,
            contentDescription = "Download",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size)
        )
    else if (downloadState.isPending || (downloadState.isDownloading && progress == -1f))
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.primary
        )
    else if (progress != -1f)
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.background,
            progress = { progress / 100 }
        )
    else
        Icon(
            imageVector = Icons.Outlined.FileDownload,
            contentDescription = "Download",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(size).bounceClick { downloadState.download() }
        )
}