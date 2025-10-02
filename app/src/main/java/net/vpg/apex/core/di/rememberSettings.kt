package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.ApexSettings
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsProvider {
    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context) = ApexSettings(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SettingsInjector {
    fun injectSettings(): ApexSettings
}

@Composable
fun rememberSettings() = rememberSettings(rememberContext())

fun rememberSettings(context: Context) = rememberInjector<SettingsInjector>(context).injectSettings()
