package net.vplaygames.apex.ui.components.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vplaygames.apex.core.rememberNavigationManager
import net.vplaygames.apex.core.di.rememberPlayer
import net.vplaygames.apex.entities.ApexTrack
import net.vplaygames.apex.ui.components.common.AlbumImage
import net.vplaygames.apex.ui.screens.NowPlayingRoute
import net.vplaygames.apex.util.apexMarquee
import net.vplaygames.apex.util.customShimmer

@Composable
fun NowPlayingBar() {
    val player = rememberPlayer()
    val navManager = rememberNavigationManager()
    if (player.nowPlaying == ApexTrack.EMPTY) return
    Row(
        modifier = Modifier
            .customShimmer(condition = player.isBuffering, durationMillis = 800)
            .clickable { navManager.navigate(NowPlayingRoute) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(player.nowPlaying.album, 40)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.nowPlaying.title,
                modifier = Modifier.apexMarquee(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
            )
            Text(
                text = player.nowPlaying.album.name,
                modifier = Modifier.apexMarquee(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
        PlayerActions(player, Modifier.padding(4.dp))
    }
}
