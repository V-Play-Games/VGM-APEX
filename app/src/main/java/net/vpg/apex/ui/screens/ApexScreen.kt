package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlin.reflect.KClass

sealed class ApexScreenDynamic<T : Any>(
    val route: KClass<T>,
    columnModifierFunction: @Composable () -> Modifier = { Modifier },
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.(T) -> Unit
) {
    private lateinit var navigateFunction: (T) -> Unit

    constructor(
        route: KClass<T>,
        columnModifier: Modifier = Modifier,
        verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        content: @Composable ColumnScope.(T) -> Unit
    ) : this(
        route,
        columnModifierFunction = { columnModifier },
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )

    private val screen: @Composable (T) -> Unit by lazy {
        @Composable { t ->
            Column(
                modifier = columnModifierFunction(),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = { content(t) }
            )
        }
    }

    fun composeTo(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable(route) { entry ->
            screen(entry.toRoute(route))
        }
        navigateFunction = { t -> navController.navigate(t) }
    }

    fun navigate(t: T) = navigateFunction(t)
}

sealed class ApexScreenStatic(
    val route: String,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    private lateinit var navigateFunction: () -> Unit

    private val screen by lazy {
        @Composable {
            Column(
                modifier = columnModifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }

    fun composeTo(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable(route) {
            screen()
        }
        navigateFunction = { navController.navigate(route) }
    }

    fun navigate() = navigateFunction()
}

sealed class ApexBottomBarScreen(
    route: String,
    val icon: ImageVector,
    val title: String,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) : ApexScreenStatic(route, columnModifier, verticalArrangement, horizontalAlignment, content)
