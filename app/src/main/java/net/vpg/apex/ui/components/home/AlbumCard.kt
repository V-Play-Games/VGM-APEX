package net.vpg.apex.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.vpg.apex.di.rememberPlayHistory
import net.vpg.apex.di.rememberPlayer
import net.vpg.apex.player.ApexTrack

@Composable
fun AlbumCard(apexTrack: ApexTrack) {
    val player = rememberPlayer()
    val playHistory = rememberPlayHistory()

    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp)
            .clickable {
                player.play(apexTrack)
                playHistory.addTrack(apexTrack)
            }
    ) {
        AlbumImage(apexTrack.album, 150)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = apexTrack.title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = apexTrack.album.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}