package net.vpg.apex.ui.components.common

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.entities.ApexTrack

@Composable
fun TrackDisplaySection(sectionTitle: String, tracks: List<ApexTrack>) {
    Column {
        Text(
            text = sectionTitle,
            modifier = Modifier.padding(start = 12.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (tracks.isEmpty()) {
            Text(
                "No recently played tracks",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        } else {
            LazyRow {
                item {
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                }
                items(tracks) {
                    TrackCard(it)
                }
            }
        }
    }
}
