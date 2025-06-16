package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.AlbumImage

object TrackInfoScreen : ApexScreenDynamic<ApexTrack>(
    route = ApexTrack::class,
    columnModifier = Modifier.composed {
        this
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    },
    content = { track ->
        @Composable
        fun InfoRow(
            icon: ImageVector,
            label: String,
            value: String
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$label Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = label,
                    fontSize = 16.sp,
                    modifier = Modifier.width(100.dp)
                )

                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Top section with album art and basic info
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = track.album.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = track.uploader.name,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Detailed info section
        Text(
            text = "Track Details",
            fontSize = 16.sp,
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

        // TODO: Add duration
        InfoRow(
            icon = Icons.Filled.AccessTime,
            label = "Duration",
            value = "--:--"
        )

        InfoRow(
            icon = Icons.Filled.CalendarToday,
            label = "Date Added",
            value = track.dateAdded
        )
    }
)
