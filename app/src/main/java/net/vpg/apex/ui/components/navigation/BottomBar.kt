package net.vpg.apex.ui.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.currentBackStackEntryAsState
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.ui.components.player.NowPlayingBar
import net.vpg.apex.ui.components.player.SeekBar
import net.vpg.apex.ui.screens.HomeScreen
import net.vpg.apex.ui.screens.LibraryScreen
import net.vpg.apex.ui.screens.NowPlayingScreen
import net.vpg.apex.ui.screens.SearchScreen

@Composable
fun BottomBar() {
    val navController = rememberNavControllerProvider().current
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        unselectedIconColor = Color.Gray,
        unselectedTextColor = Color.Gray
    )
    val screens = listOf(HomeScreen, SearchScreen, LibraryScreen)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        // Only show the NowPlayingBar if not on the NowPlayingScreen
        AnimatedVisibility(currentRoute != NowPlayingScreen.route) {
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
                    selected = currentRoute == screen.route,
                    onClick = { screen.navigate() },
                    colors = colors
                )
            }
        }
    }
}
