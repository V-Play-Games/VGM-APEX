package net.vpg.apex.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexTrackContext

@Composable
inline fun ApexTrackContext.TrackBar(
    trackIndex: Int,
    noinline onClick: () -> Unit = {},
    trailingComponents: @Composable () -> Unit = {}
) {
    val apexTrack = tracks[trackIndex]
    val player = rememberPlayer()
    val animatedColor by animateColorAsState(
        if (player.nowPlaying == apexTrack) {
            if (player.currentContext == this)
                MaterialTheme.colorScheme.primary
            else // probably a duplicate track in the same context
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else
            MaterialTheme.colorScheme.onPrimaryContainer
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .bounceClick {
                player.play(trackIndex, context = this)
                onClick()
            }
    ) {
        AlbumImage(apexTrack.album, 50)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = apexTrack.title,
                color = animatedColor,
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