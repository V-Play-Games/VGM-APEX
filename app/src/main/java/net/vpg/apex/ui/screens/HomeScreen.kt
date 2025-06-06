package net.vpg.apex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import net.vpg.apex.ui.components.home.RecentlyPlayedSection
import net.vpg.apex.ui.components.home.TrendingSection

object HomeScreen : ApexBottomBarScreen(
    route = "home",
    icon = Icons.Default.Home,
    title = "Home",
    screen = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TrendingSection()
            RecentlyPlayedSection()
        }
    }
)
