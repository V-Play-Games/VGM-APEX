package net.vplaygames.apex.ui.components.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import net.vplaygames.apex.core.rememberNavigationManager
import net.vplaygames.apex.ui.screens.ApexScreen

@Composable
fun MainScaffold(vararg screens: ApexScreen<out NavKey>) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navManager = rememberNavigationManager()

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { SideBar(drawerState) }
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = { TopBar(drawerState) },
            bottomBar = { BottomBar() },
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                entries = navManager.toEntries(entryProvider {
                    screens.forEach { it.composeTo(this) }
                }),
                onBack = { navManager.goBack() }
            )

        }
    }
}
