package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.player.ApexPlayer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerProvider {
    @Provides
    @Singleton
    fun player(@ApplicationContext context: Context) = ApexPlayer(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerInjector {
    fun player(): ApexPlayer
}

@Composable
fun rememberPlayer() = rememberInjector<PlayerInjector>().player()
