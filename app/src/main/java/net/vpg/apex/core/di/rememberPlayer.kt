package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
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
object PlayerProvider {
    @Provides
    @Singleton
    fun providePlayer(@ApplicationContext context: Context) = ApexPlayer(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerInjector {
    fun injectPlayer(): ApexPlayer
}

@Composable
fun rememberPlayer() = rememberInjector<PlayerInjector>().injectPlayer()
