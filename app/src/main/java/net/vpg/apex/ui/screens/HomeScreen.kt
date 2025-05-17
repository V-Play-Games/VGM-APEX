package net.vpg.apex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.vpg.apex.di.rememberTracksList
import net.vpg.apex.ui.components.home.RecentlyPlayedSection
import net.vpg.apex.ui.components.home.TrendingSection

@Composable
fun HomeScreen() {
    val tracksList = rememberTracksList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Add your main content here
        TrendingSection(tracksList)
        RecentlyPlayedSection(tracksList)
    }
}
