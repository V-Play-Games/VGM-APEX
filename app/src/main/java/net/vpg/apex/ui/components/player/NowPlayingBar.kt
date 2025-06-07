package net.vpg.apex.ui.components.player

import androidx.compose.foundation.background
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
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.ui.components.common.AlbumImage
import net.vpg.apex.ui.screens.NowPlayingScreen

@Composable
fun NowPlayingBar() {
    val player = rememberPlayer()
    val navController = rememberNavControllerProvider().current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                // Navigate to Now Playing Screen when bar is clicked
                navController.navigate(NowPlayingScreen.route)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(player.nowPlaying.album, 40)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.nowPlaying.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = player.nowPlaying.album.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        PlayerActions(player, Modifier.padding(4.dp))
    }
}
