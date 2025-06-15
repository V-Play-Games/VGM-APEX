package net.vpg.apex.ui.components.player

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.customShimmer
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.AlbumImage
import net.vpg.apex.ui.screens.NowPlayingScreen

@Composable
fun NowPlayingBar() {
    val player = rememberPlayer()
    if (player.nowPlaying == ApexTrack.EMPTY) return
    Row(
        modifier = (if (player.isBuffering) Modifier.customShimmer(durationMillis = 800) else Modifier)
            .clickable { NowPlayingScreen.navigate() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(player.nowPlaying.album, 40)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.nowPlaying.title,
                modifier = Modifier.basicMarquee(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
            )
            Text(
                text = player.nowPlaying.album.name,
                modifier = Modifier.basicMarquee(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
        PlayerActions(player, Modifier.padding(4.dp))
    }
}
