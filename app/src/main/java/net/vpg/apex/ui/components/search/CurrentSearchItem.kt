package net.vpg.apex.ui.components.search

import androidx.compose.runtime.Composable
import net.vpg.apex.core.di.rememberSearchHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.TrackBar

@Composable
fun CurrentSearchItem(apexTrack: ApexTrack) {
    val searchHistory = rememberSearchHistory()

    TrackBar(
        apexTrack = apexTrack,
        onClick = { searchHistory.addTrack(apexTrack) }
    )
}