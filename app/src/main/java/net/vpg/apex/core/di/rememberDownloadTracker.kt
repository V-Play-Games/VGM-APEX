package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.util.DownloadTracker
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DownloadTrackerProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @Inject
    fun provideDownloadTracker(downloadManager: DownloadManager) = DownloadTracker(downloadManager)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DownloadTrackerInjector {
    fun injectDownloadTracker(): DownloadTracker
}

@Composable
fun rememberDownloadTracker() = rememberDownloadTracker(rememberContext())

fun rememberDownloadTracker(context: Context) =
    rememberInjector<DownloadTrackerInjector>(context).injectDownloadTracker()
