package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.unwrapActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerInjector {
    @Provides
    @Singleton
    fun player(@ApplicationContext context: Context) = ExoPlayer.Builder(context).build()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerProvider {
    fun player(): ExoPlayer
}

@Composable
fun rememberPlayer() = EntryPointAccessors.fromApplication(
    LocalContext.current.unwrapActivity(),
    PlayerProvider::class.java
).player()
