package net.vpg.apex.ui.components.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.player.ApexPlayer
import net.vpg.apex.entities.ApexTrack

@Composable
fun PlayerActions(player: ApexPlayer, modifier: Modifier = Modifier) {
    PreviousButton(player, modifier)
    PlayPauseButton(player, modifier)
    NextButton(player, modifier)
    LoopButton(player, modifier)
}

@Composable
fun LoopButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    Icon(
        if (player.isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
        contentDescription = "Toggle Loop",
        tint = if (player.isLooping)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.clickable { player.isLooping = !player.isLooping }
    )
}

@Composable
fun NextButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.SkipNext,
        contentDescription = "Next",
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = if (player.canGoNext())
            modifier.clickable { player.nextTrack() }
        else
            modifier.alpha(0.5f)
    )
}

@Composable
fun PlayPauseButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    if (player.isBuffering)
        CircularProgressIndicator(
            modifier = modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary
        )
    else
        Icon(
            if (player.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "PlayPause",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = if (player.nowPlaying != ApexTrack.EMPTY)
                modifier.clickable { player.togglePlayPause() }
            else
                modifier.alpha(0.5f))
}

@Composable
fun PreviousButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.SkipPrevious,
        contentDescription = "Previous",
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = if (player.canGoPrevious())
            modifier.clickable { player.previousTrack() }
        else
            modifier.alpha(0.5f)
    )
}
