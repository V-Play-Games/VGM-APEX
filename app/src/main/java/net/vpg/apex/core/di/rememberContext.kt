package net.vpg.apex.core.di

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
import net.vpg.apex.core.unwrapActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextProvider {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context) = context
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContextInjector {
    fun injectContext(): Context
}

@Composable
fun rememberContext() = EntryPointAccessors.fromApplication(
    LocalContext.current.unwrapActivity(),
    ContextInjector::class.java
).injectContext()

@Composable
inline fun <reified T> rememberInjector() = EntryPointAccessors.fromApplication<T>(rememberContext())
