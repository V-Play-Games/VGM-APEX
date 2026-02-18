package net.vplaygames.apex.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.*

/**
 * State holder for navigation state.
 *
 * @param startRoute the start route. The user will exit the app through this route.
 * @param backStack the navigation back stack. The last item is the current route.
 */
class NavigationManager(
    val startRoute: NavKey,
    val backStack: NavBackStack<NavKey>
) {
    /**
     * Convert NavigationState into NavEntries.
     */
    @Composable
    fun toEntries(entryProvider: (NavKey) -> NavEntry<NavKey>) =
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
            entryProvider = entryProvider
        ).toMutableStateList()

    fun navigate(route: NavKey) {
        if (backStack.lastOrNull() != route) {
            backStack.add(route)
        }
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}

/**
 * Create a navigation state that persists config changes and process death.
 */
@Composable
fun rememberNavigationManager(startRoute: NavKey): NavigationManager {
    val backStack = rememberNavBackStack(startRoute)
    return remember(startRoute) {
        NavigationManager(startRoute, backStack)
    }
}

val LocalNavigationManager = compositionLocalOf<NavigationManager> { error("No NavigationManager found!") }

@Composable
fun rememberNavigationManager() = LocalNavigationManager.current
