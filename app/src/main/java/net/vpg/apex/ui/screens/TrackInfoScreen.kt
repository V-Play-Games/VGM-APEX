package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.AlbumImage
import java.text.SimpleDateFormat
import java.util.*

object TrackInfoScreen : ApexScreenDynamic<ApexTrack>(
    route = ApexTrack::class,
    content = { track ->
        TrackInfo(track)
    }
)

@Composable
fun TrackInfo(track: ApexTrack) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top section with album art and basic info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            AlbumImage(
                album = track.album,
                size = 120,
                cornerRadius = 12
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Track title and basic info
            Column {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = track.album.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = track.uploader.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Detailed info section
        Text(
            text = "Track Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Track details list
        InfoRow(
            icon = Icons.Filled.MusicNote,
            label = "Track ID",
            value = track.id
        )

        InfoRow(
            icon = Icons.Filled.Album,
            label = "Album",
            value = track.album.name
        )

        InfoRow(
            icon = Icons.Filled.Person,
            label = "Uploader",
            value = track.uploader.name
        )

        InfoRow(
            icon = Icons.Filled.AccessTime,
            label = "Duration",
            value = formatFrameLength(track.frameLength)
        )

        InfoRow(
            icon = Icons.Filled.CalendarToday,
            label = "Date Added",
            value = formatDate(track.dateAdded)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Loop Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        InfoRow(
            icon = null,
            label = "Loop Start",
            value = "${track.loopStart} frames"
        )

        InfoRow(
            icon = null,
            label = "Loop End",
            value = "${track.loopEnd} frames"
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector?,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(100.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Helper functions
private fun formatFrameLength(frameLength: Int): String {
    // Assuming 44100 frames per second for audio
    val totalSeconds = frameLength / 44100
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun formatDate(dateString: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val date = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        return dateString
    }
}