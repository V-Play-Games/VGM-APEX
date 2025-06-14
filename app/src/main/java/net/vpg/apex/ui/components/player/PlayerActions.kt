package net.vpg.apex.ui.components.player

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.bounceClick
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
        modifier = modifier.bounceClick { player.stepUpLoop() }
    )
}

@Composable
fun NextButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.SkipNext,
        contentDescription = "Next",
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = if (player.hasNextMediaItem())
            modifier.bounceClick { player.seekToNext() }
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
                modifier.bounceClick { player.togglePlayPause() }
            else
                modifier.alpha(0.5f))
}

@Composable
fun PreviousButton(player: ApexPlayer, modifier: Modifier = Modifier) {
    Icon(
        Icons.Default.SkipPrevious,
        contentDescription = "Previous",
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.bounceClick { player.seekToPrevious() }
    )
}
