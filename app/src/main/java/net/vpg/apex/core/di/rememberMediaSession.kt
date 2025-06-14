package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.player.ApexPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaSessionProvider {
    @Provides
    @Singleton
    @Inject fun provideMediaSession(@ApplicationContext context: Context, player: ApexPlayer) =
        MediaSession.Builder(context, player).build()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface MediaSessionInjector {
    fun injectMediaSession(): MediaSession
}

@Composable
fun rememberMediaSession() = rememberInjector<MediaSessionInjector>().injectMediaSession()
