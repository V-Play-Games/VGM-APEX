package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.PlayHistory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayHistoryProvider {
    @Provides
    @Singleton
    fun providePlayHistory(@ApplicationContext context: Context) = PlayHistory(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayHistoryInjector {
    fun injectPlayHistory(): PlayHistory
}

@Composable
fun rememberPlayHistory() = rememberInjector<PlayHistoryInjector>().injectPlayHistory()