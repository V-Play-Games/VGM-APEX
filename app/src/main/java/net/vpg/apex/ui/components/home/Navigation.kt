import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.vpg.apex.R

@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        unselectedIconColor = Color.Gray,
        unselectedTextColor = Color.Gray
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 },
            colors = colors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = selectedItem == 1,
            onClick = { selectedItem = 1 },
            colors = colors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Library") },
            label = { Text("Library") },
            selected = selectedItem == 2,
            onClick = { selectedItem = 2 },
            colors = colors
        )
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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