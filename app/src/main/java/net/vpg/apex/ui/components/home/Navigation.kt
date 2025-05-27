import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.vpg.apex.R
import net.vpg.apex.ui.components.NowPlayingScreen
import net.vpg.apex.ui.components.home.NowPlayingBar
import net.vpg.apex.ui.screens.HomeScreen
import net.vpg.apex.ui.screens.LibraryScreen
import net.vpg.apex.ui.screens.SearchScreen

@Composable
fun MusicAppNavigation() {
    val navController = rememberNavController()
    // Track whether to show the now playing bar
    var showNowPlayingBar by remember { mutableStateOf(true) }

    // Listen for navigation changes to control bar visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Update the showNowPlayingBar state based on current route
    showNowPlayingBar = currentRoute != ApexScreen.NowPlaying.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = { TopBar() },
        bottomBar = {
            Column {
                // Only show the NowPlayingBar if not on the NowPlayingScreen
                if (showNowPlayingBar) {
                    NowPlayingBar(navController)
                }
                BottomNavigationBar(navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ApexBottomBarScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ApexBottomBarScreen.Home.route) { HomeScreen() }
            composable(ApexBottomBarScreen.Search.route) { SearchScreen() }
            composable(ApexBottomBarScreen.Library.route) { LibraryScreen() }
            composable(ApexScreen.NowPlaying.route) { NowPlayingScreen() }
        }
    }
}

sealed class ApexScreen(val route: String) {
    object NowPlaying : ApexScreen("now_playing")
}

sealed class ApexBottomBarScreen(route: String, val icon: ImageVector, val title: String) : ApexScreen(route) {
    object Home : ApexBottomBarScreen("home", Icons.Default.Home, "Home")
    object Search : ApexBottomBarScreen("search", Icons.Default.Search, "Search")
    object Library : ApexBottomBarScreen("library", Icons.Default.LibraryMusic, "Library")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        unselectedIconColor = Color.Gray,
        unselectedTextColor = Color.Gray
    )
    val items = listOf(
        ApexBottomBarScreen.Home,
        ApexBottomBarScreen.Search,
        ApexBottomBarScreen.Library
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        // Simplify navigation to reduce potential issues
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                colors = colors
            )
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_pika_chill),
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "VGM APEX",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.displayLarge
            )
        }
        Row {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
