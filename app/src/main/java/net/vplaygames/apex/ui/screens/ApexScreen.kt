package net.vplaygames.apex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

sealed class ApexScreen<K : NavKey> {
    abstract fun composeTo(scope: EntryProviderScope<NavKey>)
}

interface ApexDynamicNavKey<T> : NavKey {
    val data: T
}

sealed class ApexScreenDynamic<T, K : ApexDynamicNavKey<T>>(
    val route: KClass<K>,
    private val columnModifier: Modifier = Modifier,
    private val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    private val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    private val content: @Composable ColumnScope.(T) -> Unit
) : ApexScreen<K>() {
    override fun composeTo(scope: EntryProviderScope<NavKey>) {
        scope.addEntryProvider(route) { key ->
            Column(
                modifier = columnModifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = { content(key.data) }
            )
        }
    }
}

sealed class ApexScreenStatic<K : NavKey>(
    val route: K,
    private val columnModifier: Modifier = Modifier,
    private val verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    private val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    private val content: @Composable ColumnScope.() -> Unit
) : ApexScreen<K>() {
    override fun composeTo(scope: EntryProviderScope<NavKey>) {
        scope.addEntryProvider(route) {
            Column(
                modifier = columnModifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }
}

sealed class ApexBottomBarScreen<K : NavKey>(
    route: K,
    val icon: ImageVector,
    val title: String,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) : ApexScreenStatic<K>(route, columnModifier, verticalArrangement, horizontalAlignment, content)
