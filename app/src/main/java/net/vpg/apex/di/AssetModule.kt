package net.vpg.apex.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.vpg.apex.player.ApexTrack
import net.vpg.vjson.parser.JSONParser.toJSON
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AssetModule {
    @Provides
    @Singleton
    fun provideTracksList(@ApplicationContext context: Context): Map<String, ApexTrack> {
        return context.assets.open("tracks.json")
            .toJSON()
            .toArray()
            .map { it.toObject() }
            .map { ApexTrack(it) }
            .associateBy { it.id }
    }
}