package net.vpg.apex.core.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProviderProvider {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDatabaseProvider(@ApplicationContext context: Context) = StandaloneDatabaseProvider(context)
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface DatabaseProviderInjector {
    @OptIn(UnstableApi::class)
    fun injectDatabaseProvider(): DatabaseProvider
}

@Composable
fun rememberDatabaseProvider() = rememberDatabaseProvider(rememberContext())

fun rememberDatabaseProvider(context: Context) = rememberInjector<DatabaseProviderInjector>(context).injectDatabaseProvider()
