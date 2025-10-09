package net.vpg.apex.entities

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

interface ApexTrackContext {
    val name: String
    val tracks: List<ApexTrack>

    @Composable
    fun ComposeToList(
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
        if (tracks.none { it != ApexTrack.EMPTY }) {
            emptyFallback()
            return
        }
        val lazyContent: LazyListScope.() -> Unit = {
            header()
            items(tracks.size.coerceAtMost(limit)) { index -> content(index) }
            footer()
        }
        if (isVertical)
            LazyColumn(modifier = modifier, state = state, content = lazyContent)
        else
            LazyRow(modifier = modifier, state = state, content = lazyContent)
    }

    companion object {
        val EMPTY: ApexTrackContext = object : ApexTrackContext {
            override val name = ""
            override val tracks = emptyList<ApexTrack>()
        }
    }
}