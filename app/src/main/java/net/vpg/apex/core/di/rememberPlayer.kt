package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.player.ApexPlayer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlayerProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun providePlayer(
        @ApplicationContext context: Context,
        dataSourceFactory: DataSource.Factory
    ) = ApexPlayer(context, DefaultMediaSourceFactory(dataSourceFactory))
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerInjector {
    fun injectPlayer(): ApexPlayer
}

@Composable
fun rememberPlayer() = rememberPlayer(rememberContext())

fun rememberPlayer(context: Context) = rememberInjector<PlayerInjector>(context).injectPlayer()
