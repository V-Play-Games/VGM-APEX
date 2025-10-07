package net.vpg.apex.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.asStateValue
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.rememberAnimationProvider
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.core.di.rememberSettings
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.ui.screens.AlbumInfoScreen

@Composable
fun AlbumBar(apexAlbum: ApexAlbum) {
    val player = rememberPlayer()
    val animationProvider = rememberAnimationProvider()
    val settings = rememberSettings()
    val gridSize = settings.gridSize.asStateValue()

    val color by animateColorAsState(
        targetValue = if (player.nowPlayingContext == apexAlbum)
            MaterialTheme.colorScheme.primary
        else if (player.nowPlaying.album == apexAlbum)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = animationProvider.mediumSpec()
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth() // don't remove
            .padding(vertical = 8.dp)
            .bounceClick { AlbumInfoScreen.navigate(apexAlbum) }
    ) {
        AlbumImage(apexAlbum, size = gridSize.albumBarImageSize, cornerRadius = 0)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = apexAlbum.name,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = apexAlbum.tracks
                    .groupBy { it.uploader.name }
                    .mapValues { it.value.size }
                    .toList()
                    .groupBy { it.second }
                    .toSortedMap(Comparator.reverseOrder())
                    .values
                    .flatten()
                    .joinToString(", ") { it.first },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
