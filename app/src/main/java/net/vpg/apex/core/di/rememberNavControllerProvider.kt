package net.vpg.apex.core.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavControllerProviderProvider {
    @Provides
    @Singleton
    fun provideNavControllerProvider() =
        compositionLocalOf<NavHostController> { error("No NavController found!") }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface NavControllerProviderInjector {
    fun injectNavControllerProvider(): ProvidableCompositionLocal<NavHostController>
}

@Composable
fun rememberNavControllerProvider() = rememberInjector<NavControllerProviderInjector>().injectNavControllerProvider()
