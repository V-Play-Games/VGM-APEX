package net.vpg.apex.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.ui.components.common.TrackCard

@Composable
fun RecentlyPlayedSection() {
    val playHistory = rememberPlayHistory()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Recently played", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        val tracks = playHistory.getTracks()
        if (tracks.isEmpty()) {
            Text(
                "No recently played tracks",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        } else {
            LazyRow {
                items(tracks.take(5)) {
                    TrackCard(it)
                }
            }
        }
    }
}
