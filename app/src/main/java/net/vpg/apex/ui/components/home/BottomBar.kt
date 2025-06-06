package net.vpg.apex.ui.components.home

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import net.vpg.apex.ui.screens.HomeScreen
import net.vpg.apex.ui.screens.LibraryScreen
import net.vpg.apex.ui.screens.SearchScreen

@Composable
fun BottomBar(navController: NavController) {
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

    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        // Simplify navigation to reduce potential issues
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                        }
                    }
                },
                colors = colors
            )
        }
    }
}
