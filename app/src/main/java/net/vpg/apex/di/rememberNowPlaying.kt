package net.vpg.apex.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.unwrapActivity
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NowPlayingInjector {
    @Provides
    @Singleton
    fun nowPlaying() = mutableStateOf(ApexTrack.EMPTY)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NowPlayingProvider {
    fun nowPlaying(): MutableState<ApexTrack>
}

@Composable
fun rememberNowPlaying() = EntryPointAccessors.fromApplication(
    LocalContext.current.unwrapActivity(),
    NowPlayingProvider::class.java
).nowPlaying()
