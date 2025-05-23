package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.player.ApexTrack
import net.vpg.vjson.parser.JSONParser.toJSON
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TracksListProvider {
    @Provides
    @Singleton
    fun tracksList(@ApplicationContext context: Context) =
        context.assets
            .open("tracks.json")
            .toJSON()
            .toArray()
            .map { ApexTrack(it.toObject()) }
            .associateBy { it.id }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TracksListInjector {
    fun tracksList(): Map<String, ApexTrack>
}

@Composable
fun rememberTracksList() = rememberInjector<TracksListInjector>().tracksList()
