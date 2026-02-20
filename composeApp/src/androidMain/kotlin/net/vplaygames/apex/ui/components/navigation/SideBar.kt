package net.vplaygames.apex.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import net.vplaygames.apex.R
import net.vplaygames.apex.core.rememberNavigationManager
import net.vplaygames.apex.ui.screens.HomeRoute
import net.vplaygames.apex.ui.screens.LibraryRoute
import net.vplaygames.apex.ui.screens.ProfileRoute
import net.vplaygames.apex.ui.screens.SearchRoute
import net.vplaygames.apex.ui.screens.SettingsRoute

@Composable
fun SideBar(drawerState: DrawerState) {
    val navManager = rememberNavigationManager()
    val scope = rememberCoroutineScope()

    fun navigateTo(route: NavKey) {
        scope.launch { drawerState.close() }
        navManager.navigate(route)
    }

    DismissibleDrawerSheet(drawerState = drawerState) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .clickable { navigateTo(ProfileRoute) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_giratina_chill),
                contentDescription = "App Logo",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "VGM APEX",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "View Profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = false,
            onClick = { navigateTo(HomeRoute) },
        )
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            label = {
                Text(
                    text = "Search",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = false,
            onClick = { navigateTo(SearchRoute) },
        )
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = "Library",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            label = {
                Text(
                    text = "Library",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = false,
            onClick = { navigateTo(LibraryRoute) },
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { navigateTo(SettingsRoute) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
