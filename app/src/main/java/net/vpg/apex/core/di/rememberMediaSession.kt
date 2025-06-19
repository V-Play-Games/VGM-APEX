package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.player.ApexPlayer
import net.vpg.apex.core.player.CoilBitmapLoader
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaSessionProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    @Inject
    fun provideMediaSession(
        player: ApexPlayer,
        @ApplicationContext context: Context
    ) = MediaSession.Builder(context, player)
        .setBitmapLoader(CoilBitmapLoader(context))
        .build()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface MediaSessionInjector {
    fun injectMediaSession(): MediaSession
}

@Composable
fun rememberMediaSession() = rememberMediaSession(rememberContext())

fun rememberMediaSession(context: Context) = rememberInjector<MediaSessionInjector>(context).injectMediaSession()
