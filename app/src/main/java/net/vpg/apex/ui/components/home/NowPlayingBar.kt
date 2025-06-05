package net.vpg.apex.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.vpg.apex.di.rememberPlayer
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.ui.screens.NowPlayingScreen

@Composable
fun NowPlayingBar(navController: NavController) {
    val player = rememberPlayer()
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp))
        )
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
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.Default.SkipPrevious,
            contentDescription = "Previous",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = if (player.canGoPrevious())
                Modifier.clickable { player.previousTrack() }
            else
                Modifier.alpha(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (player.isBuffering)
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
        else
            Icon(
                if (player.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "PlayPause",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = if (player.nowPlaying != ApexTrack.EMPTY)
                    Modifier.clickable { player.togglePlayPause() }
                else
                    Modifier.alpha(0.5f))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.Default.SkipNext,
            contentDescription = "Next",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = if (player.canGoNext())
                Modifier.clickable { player.nextTrack() }
            else
                Modifier.alpha(0.5f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            if (player.isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
            contentDescription = "Toggle Loop",
            tint = if (player.isLooping)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.clickable { player.isLooping = !player.isLooping }
        )
    }
}