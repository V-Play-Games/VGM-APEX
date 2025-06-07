package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.TrackDisplaySection

object HomeScreen : ApexBottomBarScreen(
    route = "home",
    icon = Icons.Default.Home,
    title = "Home",
    content = {
        val playHistory = rememberPlayHistory()
        val trending = remember { ApexTrack.TRACKS_DB.values.shuffled() }

        TrackDisplaySection("Trending", trending.take(5))
        Spacer(modifier = Modifier.height(16.dp))
        TrackDisplaySection("Recently Played", playHistory.getTracks().take(5))
    }
)
