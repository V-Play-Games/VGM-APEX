package net.vpg.apex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlin.reflect.KClass

sealed class ApexScreenWithParameters<T : Any>(
    val route: KClass<T>,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.(T) -> Unit
) {
    private val screen: @Composable (T) -> Unit by lazy {
        @Composable { t ->
            Column(
                modifier = columnModifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = { content(t) }
            )
        }
    }

    fun composeTo(builder: NavGraphBuilder) {
        builder.composable(route) { entry ->
            screen(entry.toRoute(route))
        }
    }
}

sealed class ApexScreen(
    val route: String,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    private val screen by lazy {
        @Composable {
            Column(
                modifier = columnModifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }

    fun composeTo(builder: NavGraphBuilder) = builder.composable(route) { screen() }
}

sealed class ApexBottomBarScreen(
    route: String,
    val icon: ImageVector,
    val title: String,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) : ApexScreen(route, columnModifier, verticalArrangement, horizontalAlignment, content)
