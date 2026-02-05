package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.NavigationState
import net.vpg.apex.core.Navigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationStateProviderProvider {
    @Provides
    @Singleton
    fun provideNavigatorStateProvider(@ApplicationContext context: Context) =
        compositionLocalOf<NavigationState> { error("No NavController found!") }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface NavigationStateProviderInjector {
    fun injectNavigationStateProvider(): ProvidableCompositionLocal<NavigationState>
}

@Composable
fun rememberNavigationStateProvider() = rememberNavigationStateProvider(rememberContext())

@Composable
fun rememberNavigationState() = rememberNavigationStateProvider().current

@Composable
fun rememberNavigator() = rememberNavigationState().let { remember(it) { Navigator(it) } }

fun rememberNavigationStateProvider(context: Context) =
    rememberInjector<NavigationStateProviderInjector>(context).injectNavigationStateProvider()
