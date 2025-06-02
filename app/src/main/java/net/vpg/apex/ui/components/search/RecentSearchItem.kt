package net.vpg.apex.ui.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.vpg.apex.di.rememberSearchHistory
import net.vpg.apex.player.ApexTrack

@Composable
fun RecentSearchItem(apexTrack: ApexTrack) {
    val searchHistory = rememberSearchHistory()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TrackBar(
            apexTrack = apexTrack,
            trailingComponents = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { searchHistory.removeTrack(apexTrack) },
                    tint = Color.DarkGray
                )
            }
        )
    }
}
