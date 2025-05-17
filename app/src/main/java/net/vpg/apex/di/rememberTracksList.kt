package net.vpg.apex.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.unwrapActivity
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
            .map { it.toObject() }
            .map { ApexTrack(it) }
            .onEach { println(it) }
            .associateBy { it.id }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TracksListInjector {
    fun tracksList(): Map<String, ApexTrack>
}

@Composable
fun rememberTracksList() = EntryPointAccessors.fromApplication(
    LocalContext.current.unwrapActivity(),
    TracksListInjector::class.java
).tracksList()

