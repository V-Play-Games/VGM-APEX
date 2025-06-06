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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.TrackCard

@Composable
fun TrendingSection() {
    val trending = remember {
        ApexTrack.TRACKS_DB.values.shuffled()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Trending", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(trending.take(5)) {
                TrackCard(it)
            }
        }
    }
}
