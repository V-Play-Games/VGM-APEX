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
import net.vpg.apex.util.bounceClick
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.core.di.rememberSetting
import net.vpg.apex.core.rememberAnimationProvider
import net.vpg.apex.entities.ApexTrackContext

@Composable
fun ApexTrackContext.TrackCard(trackIndex: Int) {
    val apexTrack = tracks[trackIndex]
    val player = rememberPlayer()
    val gridSize = rememberSetting { gridSize }
    val animatedColor by animateColorAsState(
        targetValue = if (player.nowPlaying != apexTrack)
            MaterialTheme.colorScheme.onPrimaryContainer
        else if (player.nowPlayingContext == this)
            MaterialTheme.colorScheme.primary
        else // probably a duplicate track in the same context
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        animationSpec = rememberAnimationProvider().mediumSpec()
    )

    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(gridSize.cardSize.dp)
            .bounceClick { player.play(trackIndex, context = this) }
    ) {
        AlbumImageWithInfoButton(
            album = apexTrack.album,
            size = gridSize.cardSize,
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