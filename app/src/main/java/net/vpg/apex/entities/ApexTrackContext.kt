package net.vpg.apex.entities

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlin.math.min

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
        limit: Int = tracks.size,
        emptyFallback: @Composable () -> Unit = {
            Text(
                text = "No tracks found in $name",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        lazyComposable: @Composable (LazyListScope.() -> Unit) -> Unit,
        content: @Composable ApexTrackContext.(Int) -> Unit
    ) {
        if (tracks.none { it != ApexTrack.EMPTY }) {
            emptyFallback()
            return
        }
        lazyComposable {
            items(min(limit, tracks.size)) { index ->
                content(index)
            }
        }
    }

    companion object {
        val EMPTY: ApexTrackContext = object : ApexTrackContext {
            override val name = ""
            override val tracks = emptyList<ApexTrack>()
        }
    }
}