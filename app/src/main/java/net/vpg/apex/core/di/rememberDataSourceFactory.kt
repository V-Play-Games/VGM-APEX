package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
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
class DataSourceFactoryProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @Inject
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
        downloadCache: SimpleCache,
    ): DataSource.Factory = CacheDataSource.Factory()
        .setCache(downloadCache)
        .setUpstreamDataSourceFactory(
            CacheDataSource.Factory()
                .setCache(
                    SimpleCache(
                        File(context.cacheDir, "exo_cache"),
                        LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024), // 512 MB
                        databaseProvider
                    )
                )
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        )
        .setCacheWriteDataSinkFactory(null)

}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DataSourceFactoryInjector {
    fun injectDataSourceFactory(): DataSource.Factory
}

@Composable
fun rememberDataSourceFactory() = rememberDataSourceFactory(rememberContext())

fun rememberDataSourceFactory(context: Context) =
    rememberInjector<DataSourceFactoryInjector>(context).injectDataSourceFactory()
