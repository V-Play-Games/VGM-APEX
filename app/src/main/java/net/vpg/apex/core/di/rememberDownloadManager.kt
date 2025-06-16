package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DownloadManagerProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @Inject
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
        downloadCache: SimpleCache,
        upstreamFactory: DataSource.Factory
    ) = DownloadManager(
        context,
        databaseProvider,
        downloadCache,
        upstreamFactory,
        Executors.newFixedThreadPool(6)
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DownloadManagerInjector {
    @OptIn(UnstableApi::class)
    fun injectDownloadManager(): DownloadManager
}

@Composable
fun rememberDownloadManager() = rememberDownloadManager(rememberContext())

fun rememberDownloadManager(context: Context) =
    rememberInjector<DownloadManagerInjector>(context).injectDownloadManager()
