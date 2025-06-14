package net.vpg.apex.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexTrack

@Composable
fun TrackCard(apexTrack: ApexTrack) {
    val player = rememberPlayer()
    val playHistory = rememberPlayHistory()
    val animatedColor by animateColorAsState(
        if (player.nowPlaying == apexTrack)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onPrimaryContainer
    )

    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp)
            .bounceClick {
                player.play(apexTrack)
                playHistory.addTrack(apexTrack)
            }
    ) {
        AlbumImageWithInfoButton(
            album = apexTrack.album,
            size = 150,
            apexTrack = apexTrack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = apexTrack.title,
            color = animatedColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = apexTrack.album.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}