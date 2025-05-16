package net.vpg.apex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.vpg.apex.ui.components.home.EditorsPicksSection
import net.vpg.apex.ui.components.home.RecentlyPlayedSection

@Composable

fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Add your main content here
        EditorsPicksSection()
        RecentlyPlayedSection()
    }
}
