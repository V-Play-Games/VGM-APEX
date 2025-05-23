package net.vpg.apex.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import net.vpg.apex.di.rememberPlayer
import net.vpg.apex.player.ApexTrack

@Composable
fun NowPlayingBar() {
    val player = rememberPlayer()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
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
                text = player.nowPlaying.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = player.nowPlaying.category,
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
            if (player.isLooping.value) Icons.Default.RepeatOne else Icons.Default.Repeat,
            contentDescription = "Toggle Loop",
            tint = if (player.isLooping.value)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.clickable { player.isLooping.value = !player.isLooping.value }
        )
    }
}
