package net.vpg.apex.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.entities.ApexTrackContext

@Composable
fun ApexTrackContext.TrackDisplaySection(trailingComponents: @Composable () -> Unit = {}) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            trailingComponents()
        }
        Spacer(modifier = Modifier.height(8.dp))
        ComposeToList(
            limit = 5,
            emptyFallback = {
                Text(
                    text = "Nothing to see here :(",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            },
            lazyComposable = {
                LazyRow {
                    item {
                        Spacer(modifier = Modifier.padding(start = 12.dp))
                    }
                    it()
                }
            },
            content = { trackIndex ->
                TrackCard(trackIndex)
            }
        )
    }
}
