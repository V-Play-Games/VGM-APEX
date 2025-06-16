package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.SearchHistory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchHistoryProvider {
    @Provides
    @Singleton
    fun provideSearchHistory(@ApplicationContext context: Context) = SearchHistory(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface SearchHistoryInjector {
    fun injectSearchHistory(): SearchHistory
}

@Composable
fun rememberSearchHistory() = rememberSearchHistory(rememberContext())

fun rememberSearchHistory(context: Context) = rememberInjector<SearchHistoryInjector>(context).injectSearchHistory()
