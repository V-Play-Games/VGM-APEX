package net.vpg.apex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.customShimmer
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.ui.components.common.AlbumImageWithInfoButton
import net.vpg.apex.ui.components.common.TrackBar
import net.vpg.apex.ui.components.common.TrackDownloadIcon
import net.vpg.apex.ui.components.player.PlayerActions
import net.vpg.apex.ui.components.player.SeekBar

@OptIn(ExperimentalMaterial3Api::class)
object NowPlayingScreen : ApexScreenStatic(
    route = "now_playing",
    columnModifier = Modifier.padding(horizontal = 12.dp),
    content = {
        val player = rememberPlayer()
        val nowPlaying = player.nowPlaying
        var showBottomSheet by remember { mutableStateOf(false) }

        Text(
            text = "Playing from ${player.nowPlayingContext.name}",
            modifier = Modifier.customShimmer(condition = player.isPlaying, durationMillis = 800)
                .align(Alignment.CenterHorizontally)
                .padding(6.dp)
                .bounceClick { showBottomSheet = true },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        AlbumImageWithInfoButton(
            album = nowPlaying.album,
            size = 360,
            apexTrack = nowPlaying
        )

        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
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
            }
            TrackDownloadIcon(nowPlaying, 40.dp)
        }

        Spacer(Modifier.height(16.dp))

        SeekBar(bottomBar = false)

        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth() // don't remove
        ) {
            PlayerActions(player, Modifier.size(30.dp))
        }

        AnimatedVisibility(showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Text(
                    text = "Queue",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                // Add your queue list here
                player.queueContext.ComposeToList(
                    emptyFallback = {
                        Text(
                            text = "Nothing in the queue",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    lazyComposable = { list ->
                        LazyColumn(modifier = Modifier.padding(horizontal = 12.dp)) {
                            list()
                        }
                    },
                    content = { trackIndex ->
                        if (trackIndex >= player.nowPlayingIndex)
                            TrackBar(trackIndex)
                    }
                )
            }
        }
    }
)
