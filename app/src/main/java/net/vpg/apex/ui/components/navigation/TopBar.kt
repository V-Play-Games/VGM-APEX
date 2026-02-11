package net.vpg.apex.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.vpg.apex.R
import net.vpg.apex.core.rememberNavigationManager
import net.vpg.apex.ui.screens.SettingsRoute
import net.vpg.apex.util.bounceClick

@Composable
fun TopBar(drawerState: DrawerState) {
    val navManager = rememberNavigationManager()
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth() // don't remove
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_giratina_chill),
                contentDescription = "Open Navigation Drawer",
                modifier = Modifier
                    .size(40.dp)
                    .bounceClick {
                        scope.launch { drawerState.open() }
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "VGM APEX",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        }
        Row {
            Icon(
                Icons.Default.Settings,
                modifier = Modifier.bounceClick(onClick = { navManager.navigate(SettingsRoute) }),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
