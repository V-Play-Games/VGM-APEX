package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import net.vpg.apex.core.ApexSetting
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

@Composable
inline fun <reified T> rememberSetting(settingExtractor: ApexSettings.() -> Flow<T>): T
        where T : Enum<T>, T : ApexSetting =
    settingExtractor(rememberSettings()).collectAsState(initial = enumValues<T>().first()).value

@Composable
fun rememberFloatSetting(initialValue: Float = 1f, settingExtractor: ApexSettings.() -> Flow<Float>) =
    settingExtractor(rememberSettings()).collectAsState(initialValue).value

fun rememberSettings(context: Context) = rememberInjector<SettingsInjector>(context).injectSettings()
