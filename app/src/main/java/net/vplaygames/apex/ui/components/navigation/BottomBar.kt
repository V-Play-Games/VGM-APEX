package net.vplaygames.apex.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.vplaygames.apex.core.rememberNavigationManager
import net.vplaygames.apex.ui.components.player.NowPlayingBar
import net.vplaygames.apex.ui.components.player.SeekBar
import net.vplaygames.apex.ui.screens.HomeScreen
import net.vplaygames.apex.ui.screens.LibraryScreen
import net.vplaygames.apex.ui.screens.NowPlayingRoute
import net.vplaygames.apex.ui.screens.SearchScreen

@Composable
fun BottomBar() {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        unselectedIconColor = Color.Gray,
        unselectedTextColor = Color.Gray
    )
    val screens = listOf(HomeScreen, SearchScreen, LibraryScreen)
    val navManager = rememberNavigationManager()
    val destination = navManager.backStack.last()

    Column {
        // Only show the NowPlayingBar if not on the NowPlayingScreen
        AnimatedVisibility(destination !is NowPlayingRoute) {
            Box {
                NowPlayingBar()
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                        .offset(y = 20.dp)
                ) {
                    SeekBar(bottomBar = true)
                }
            }
        }
        NavigationBar(containerColor = Color.Transparent) {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = destination == screen.route,
                    onClick = { navManager.navigate(screen.route) },
                    colors = colors
                )
            }
        }
    }
}
