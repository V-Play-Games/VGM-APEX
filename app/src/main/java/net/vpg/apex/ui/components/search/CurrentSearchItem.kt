package net.vpg.apex.ui.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.vpg.apex.di.rememberSearchHistory
import net.vpg.apex.player.ApexTrack

@Composable
fun CurrentSearchItem(apexTrack: ApexTrack) {
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
            onClick = { println("gurt: Yo"); searchHistory.addTrack(apexTrack) }
        )
    }
}