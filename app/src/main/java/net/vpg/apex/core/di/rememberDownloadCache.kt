package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DownloadCacheProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @Inject
    fun provideDownloadCache(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider
    ) = SimpleCache(
        File(context.filesDir, "downloads"),
        NoOpCacheEvictor(),
        databaseProvider
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DownloadCacheInjector {
    @OptIn(UnstableApi::class)
    fun injectDownloadCache(): SimpleCache
}

@Composable
fun rememberDownloadCache() = rememberDownloadCache(rememberContext())

fun rememberDownloadCache(context: Context) = rememberInjector<DownloadCacheInjector>(context).injectDownloadCache()
