package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexTrackContext
import net.vpg.apex.ui.components.common.TrackDisplaySection

object HomeScreen : ApexBottomBarScreen(
    route = "home",
    icon = Icons.Default.Home,
    title = "Home",
    content = {
        val playHistory = rememberPlayHistory()
        val random = object : ApexTrackContext {
            override val name = "Random Picks"
            override val tracks = ApexTrack.TRACKS_DB.values.shuffled().take(5)
        }

        random.TrackDisplaySection()
        Spacer(modifier = Modifier.height(32.dp))
        playHistory.TrackDisplaySection()
    }
)
