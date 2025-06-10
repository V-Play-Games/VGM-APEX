package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.TrackHistory
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.TrackDisplaySection

object HomeScreen : ApexBottomBarScreen(
    route = "home",
    icon = Icons.Default.Home,
    title = "Home",
    content = {
        val playHistory = rememberPlayHistory()
        val trending = remember(Unit) {
            TrackHistory(ApexTrack.TRACKS_DB.values.shuffled().take(5))
        }

        TrackDisplaySection("Trending", trending)
        Spacer(modifier = Modifier.height(32.dp))
        TrackDisplaySection("Recently Played", playHistory)
    }
)
