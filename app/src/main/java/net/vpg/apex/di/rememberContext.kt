package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.unwrapActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextProvider {
    @Provides
    @Singleton
    fun context(@ApplicationContext context: Context) = context
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContextInjector {
    fun context(): Context
}

@Composable
fun rememberContext() = EntryPointAccessors.fromApplication(
    LocalContext.current.unwrapActivity(),
    ContextInjector::class.java
).context()

@Composable
inline fun <reified T> rememberInjector() = EntryPointAccessors.fromApplication<T>(rememberContext())
