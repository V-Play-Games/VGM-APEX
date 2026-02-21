package net.vplaygames.apex.entities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// All possible contexts are:
// - Album Info
// - Uploader Info
// - Random Picks
// - Play History
// - Search History
// - Search Results

@Composable
fun ApexTrackContext.ComposeToList(
    modifier: Modifier = Modifier,
    limit: Int = tracks.size,
    emptyFallback: @Composable () -> Unit = {
        Text(
            text = "No tracks found in $name",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    },
    state: LazyListState = rememberLazyListState(),
    isVertical: Boolean = true,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
    content: @Composable ApexTrackContext.(Int) -> Unit
) {
    val visibleTracks = tracks.take(limit)
    if (visibleTracks.isEmpty()) {
        emptyFallback()
        return
    }
    val lazyContent: LazyListScope.() -> Unit = {
        header()
        items(
            count = visibleTracks.size,
            key = { visibleTracks[it].id }
        ) { index ->
            Box(modifier = Modifier.animateItem()) {
                content(tracks.indexOf(visibleTracks[index]))
            }
        }
        footer()
    }
    if (isVertical)
        LazyColumn(modifier = modifier, state = state, content = lazyContent)
    else
        LazyRow(modifier = modifier, state = state, content = lazyContent)
}