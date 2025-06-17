package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.RandomPicks
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberPlayHistory
import net.vpg.apex.ui.components.common.TrackDisplaySection

object HomeScreen : ApexBottomBarScreen(
    route = "home",
    icon = Icons.Default.Home,
    title = "Home",
    content = {
        val playHistory = rememberPlayHistory()

        RandomPicks.currentPicks.TrackDisplaySection {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Home",
                modifier = Modifier.bounceClick { RandomPicks.refresh() }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        playHistory.TrackDisplaySection()
    }
)
