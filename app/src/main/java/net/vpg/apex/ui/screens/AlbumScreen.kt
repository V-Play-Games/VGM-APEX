package net.vpg.apex.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.core.di.rememberPlayer
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.ui.components.common.AlbumImage
import net.vpg.apex.ui.components.common.TrackBar

object AlbumInfoScreen : ApexScreenWithParameters<ApexAlbum>(
    route = ApexAlbum::class,
    content = { album ->
        AlbumInfo(album)
    }
)

@SuppressLint("RestrictedApi")
@Composable
fun AlbumInfo(album: ApexAlbum) {
    val navController = rememberNavControllerProvider().current
    val player = rememberPlayer()

    // Add at the top of the AlbumInfo function
    val scrollState = rememberLazyListState()
    val headerMaxHeight = 320.dp
    val headerMinHeight = 120.dp

    // Calculate the size based on scroll position
    val scrollOffset by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset.toFloat() } }
    val firstItemIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    val imageSize = if (firstItemIndex == 0) {
        // Map scroll offset to a size between max and min height
        val scrollProgress = (scrollOffset / 600).coerceIn(0f, 1f)
        lerp(headerMaxHeight.value, headerMinHeight.value, scrollProgress).dp
    } else {
        // If we've scrolled past the first item, use the minimum size
        headerMinHeight
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize()
    ) {
        // Album Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // Album Art
                Box(modifier = Modifier.align(BiasAlignment(0f, 0.5f))) {
                    AlbumImage(album, imageSize.value.toInt(), 4)
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC000000)
                                ),
                                startY = 0f,
                                endY = 320f
                            )
                        )
                )

                // Back button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }


                // Play button row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Album info
                    Column {
                        Text(
                            text = album.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Added on ${album.dateAdded}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${album.tracks.size} tracks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                    IconButton(
                        onClick = {
                            if (album.tracks.isNotEmpty()) {
                                player.play(album.tracks.first())
                            }
                        },
                        modifier = Modifier.size(54.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        }

        // Track list
        item {
            Text(
                text = "Tracks",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(album.tracks) { track ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TrackBar(track)
            }
        }

        // Add some space at the bottom
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}