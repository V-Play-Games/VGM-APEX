package net.vpg.apex.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexTrack

@Composable
fun TrackCard(apexTrack: ApexTrack) {
    val player = rememberPlayer()
    val playHistory = rememberPlayHistory()
    val navController = rememberNavControllerProvider().current

    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp)
            .clickable {
                player.play(apexTrack)
                playHistory.addTrack(apexTrack)
            }
    ) {
        AlbumImageWithInfoButton(apexTrack.album, 150, onClick = {
            navController.navigate(apexTrack)
        })
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