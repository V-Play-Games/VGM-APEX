package net.vpg.apex.ui.screens

import ApexScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.vpg.apex.di.rememberPlayer
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.ui.components.home.AlbumImage
import kotlin.math.max

object NowPlayingScreen : ApexScreen(
    route = "now_playing",
    screen = {
        val player = rememberPlayer()
        val nowPlaying = player.nowPlaying
        var position by remember { mutableLongStateOf(player.currentPosition) }
        val duration = max(0 ,player.duration)
        val progress = if (duration > 0) position.toFloat() / duration else 0f

        LaunchedEffect(Unit) {
            while (true) {
                position = player.currentPosition
                delay(1000) // Delay for 1 second
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumImage(nowPlaying.album, 360)

            Spacer(Modifier.height(24.dp))

            Text(
                text = nowPlaying.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = nowPlaying.album.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(16.dp))

            Slider(
                value = progress,
                onValueChange = { /*player.seekTo((it * duration).toLong())*/ },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(player.currentPosition),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = formatDuration(player.duration),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Previous",
                    tint = if (player.isShuffling)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp).clickable { player.isShuffling = !player.isShuffling }
                )
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = if (player.canGoPrevious())
                        Modifier.size(30.dp).clickable { player.previousTrack() }
                    else
                        Modifier.size(30.dp).alpha(0.5f)
                )
                if (player.isBuffering)
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                else
                    Icon(
                        if (player.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "PlayPause",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = if (player.nowPlaying != ApexTrack.EMPTY)
                            Modifier.size(30.dp).clickable { player.togglePlayPause() }
                        else
                            Modifier.size(30.dp).alpha(0.5f))
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = if (player.canGoNext())
                        Modifier.size(30.dp).clickable { player.nextTrack() }
                    else
                        Modifier.size(30.dp).alpha(0.5f)
                )
                Icon(
                    if (player.isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Toggle Loop",
                    tint = if (player.isLooping)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp).clickable { player.isLooping = !player.isLooping }
                )
            }
        }
    }
)

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
