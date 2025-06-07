package net.vpg.apex.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexTrack

@Composable
inline fun TrackBar(
    apexTrack: ApexTrack,
    noinline onClick: () -> Unit = {},
    trailingComponents: @Composable () -> Unit = {}
) {
    val player = rememberPlayer()
    val playHistory = rememberPlayHistory()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                player.play(apexTrack)
                playHistory.addTrack(apexTrack)
                onClick()
            }
    ) {
        AlbumImage(apexTrack.album, 50)
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = apexTrack.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = apexTrack.album.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        trailingComponents()
    }
}