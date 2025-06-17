package net.vpg.apex.ui.components.player

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.vpg.apex.core.customShimmer
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.core.formatDuration
import net.vpg.apex.entities.ApexTrack
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(bottomBar: Boolean) {
    val player = rememberPlayer()
    if (player.nowPlaying == ApexTrack.EMPTY) return
    var position by remember { mutableLongStateOf(player.currentPosition) }
    val duration = max(0, player.duration)
    val progress = if (position > 0 && duration > 0) position.toFloat() / duration else 0f
    var isDragging by remember { mutableStateOf(false) }
    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    )
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        while (true) {
            if (!isDragging) {
                position = player.currentPosition
            }
            delay(100) // Update every 100 ms
        }
    }

    Slider(
        value = progress,
        modifier = Modifier.customShimmer(condition = player.isBuffering, durationMillis = 400),
        onValueChange = { newProgress ->
            isDragging = true
            position = (newProgress * duration).toLong()
        },
        onValueChangeFinished = {
            player.seekTo((progress * duration).toLong())
            isDragging = false
        },
        colors = sliderColors,
        interactionSource = interactionSource,
        thumb = {
            if (!bottomBar)
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    colors = sliderColors,
                    thumbSize = DpSize(3.5.dp, 35.dp)
                )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(4.dp),
                sliderState = sliderState,
                colors = sliderColors,
                drawStopIndicator = {},
                thumbTrackGapSize = if (bottomBar) 0.dp else 3.dp
            )
        }
    )
    if (!bottomBar) {
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), // don't remove
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
    }
}