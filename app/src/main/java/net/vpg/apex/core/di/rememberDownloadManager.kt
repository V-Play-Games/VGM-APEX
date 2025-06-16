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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.util.DownloadUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadManagerProvider {
    @Provides
    @Singleton
    fun provideDownloadManager(@ApplicationContext context: Context) = DownloadUtil.getDownloadManager(context)
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
