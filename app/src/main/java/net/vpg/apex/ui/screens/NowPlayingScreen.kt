package net.vpg.apex.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.ui.components.common.AlbumImageWithInfoButton
import net.vpg.apex.ui.components.player.PlayerActions
import net.vpg.apex.ui.components.player.SeekBar

@OptIn(ExperimentalMaterial3Api::class)
object NowPlayingScreen : ApexScreenStatic(
    route = "now_playing",
    columnModifier = Modifier.padding(horizontal = 12.dp),
    content = {
        val player = rememberPlayer()
        val nowPlaying = player.nowPlaying

        AlbumImageWithInfoButton(
            album = nowPlaying.album,
            size = 360,
            apexTrack = nowPlaying
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = nowPlaying.title,
            modifier = Modifier.basicMarquee(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = nowPlaying.album.name,
            modifier = Modifier
                .basicMarquee()
                .clickable { AlbumInfoScreen.navigate(nowPlaying.album) },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
        )

        Spacer(Modifier.height(16.dp))

        SeekBar(bottomBar = false)

        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth() // don't remove
        ) {
            Icon(
                Icons.Default.Shuffle,
                contentDescription = "Previous",
                tint = if (player.isShuffling)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(30.dp)
                    .bounceClick { player.toggleShuffling() }
            )
            PlayerActions(player, Modifier.size(30.dp))
        }
    }
)
