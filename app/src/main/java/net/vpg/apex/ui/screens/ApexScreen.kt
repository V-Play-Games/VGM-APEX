package net.vpg.apex.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

open class ApexScreen(val route: String, val screen: @Composable () -> Unit) {
    fun composeTo(builder: NavGraphBuilder) = builder.composable(route) { screen() }
}

open class ApexBottomBarScreen(
    route: String,
    val icon: ImageVector,
    val title: String,
    screen: @Composable () -> Unit
) : ApexScreen(route, screen)
