package net.vpg.apex.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.core.NotificationMaker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationMakerProvider {
    @Provides
    @Singleton
    fun provideNotificationMaker(@ApplicationContext context: Context) = NotificationMaker()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface NotificationMakerInjector {
    fun injectNotificationMaker(): NotificationMaker
}

@Composable
fun rememberNotificationMaker() = rememberInjector<NotificationMakerInjector>().injectNotificationMaker()
