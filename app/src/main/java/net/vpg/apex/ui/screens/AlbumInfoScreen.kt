package net.vpg.apex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.ui.components.common.AlbumImage
import net.vpg.apex.ui.components.common.TrackBar
import net.vpg.apex.ui.components.common.TrackDownloadIcon

object AlbumInfoScreen : ApexScreenDynamic<ApexAlbum>(
    route = ApexAlbum::class,
    content = { album ->
        val player = rememberPlayer()

        // Add at the top of the AlbumInfo function
        val scrollState = rememberLazyListState()
        val headerMaxHeight = 320
        val headerMinHeight = 120

        // Calculate the size based on scroll position
        val scrollOffset by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset.toFloat() } }
        val firstItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
        val imageSize = if (firstItemIndex == 0) {
            // Map scroll offset to a size between max and min height
            val scrollProgress = (scrollOffset / 600).coerceIn(0f, 1f)
            lerp(headerMaxHeight, headerMinHeight, scrollProgress)
        } else {
            // If we've scrolled past the first item, use the minimum size
            headerMinHeight
        }

        val titleRow = @Composable {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // don't remove
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = album.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Added on ${album.dateAdded}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${album.tracks.size} tracks",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (album.tracks.isEmpty())
                    return@Row
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .bounceClick {
                            if (player.currentContext != album)
                                player.play(0, album)
                            else
                                player.togglePlayPause()
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (player.isPlaying && player.currentContext == album)
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        }

        // Track list
        album.ComposeToList(
            emptyFallback = {
                Box(
                    modifier = Modifier.fillMaxSize(), // don't remove
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks found in this album",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            },
            lazyComposable = { list ->
                LazyColumn(state = scrollState) {
                    // Album Header
                    item {
                        Box(modifier = Modifier.height(320.dp)) {
                            // Album Art
                            Box(modifier = Modifier.align(BiasAlignment(0f, 0.75f))) {
                                AlbumImage(album, imageSize, 4)
                            }

                            // Gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize() // don't remove
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.background.copy(alpha = 0.75f)
                                            )
                                        )
                                    )
                            )

                            // Play button row
                            Box(modifier = Modifier.align(Alignment.BottomStart)) {
                                titleRow()
                            }
                        }
                    }
                    stickyHeader {
                        AnimatedVisibility(firstItemIndex != 0) {
                            Box(
                                modifier = Modifier.background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.background,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                            Color.Transparent,
                                        )
                                    )
                                )
                            ) {
                                titleRow()
                            }
                        }
                    }
                    list()
                }
            },
            content = { trackIndex ->
                TrackBar(trackIndex) {
                    TrackDownloadIcon(album.tracks[trackIndex])
                }
            }
        )

    }
)