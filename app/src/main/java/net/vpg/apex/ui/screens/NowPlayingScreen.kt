package net.vpg.apex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.core.formatDuration
import net.vpg.apex.ui.components.common.AlbumImageWithInfoButton
import net.vpg.apex.ui.components.player.PlayerActions
import kotlin.math.max

object NowPlayingScreen : ApexScreen(
    route = "now_playing",
    columnModifier = Modifier.padding(16.dp),
    content = {
        val player = rememberPlayer()
        val nowPlaying = player.nowPlaying
        var position by remember { mutableLongStateOf(player.currentPosition) }
        val duration = max(0, player.duration)
        val progress = if (duration > 0) position.toFloat() / duration else 0f
        var isDragging by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            while (true) {
                if (!isDragging) {
                    position = player.currentPosition
                }
                delay(1000) // Update every 1 second
            }
        }
        AlbumImageWithInfoButton(nowPlaying.album, 360)

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
            onValueChange = { newProgress ->
                isDragging = true
                position = (newProgress * duration).toLong()
            },
            onValueChangeFinished = {
                // Only seek when user releases the slider to avoid too many seek operations
                player.seekTo((progress * duration).toLong())
                isDragging = false
            },
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
                text = formatDuration(position),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                text = formatDuration(duration),
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
            PlayerActions(player, Modifier.size(30.dp))
        }
    }
)
