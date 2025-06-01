package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.SearchHistory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchHistoryProvider {
    @Provides
    @Singleton
    fun provider(@ApplicationContext context: Context) = SearchHistory(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SearchHistoryInjector {
    fun inject(): SearchHistory
}

@Composable
fun rememberSearchHistory() = rememberInjector<SearchHistoryInjector>().inject()
