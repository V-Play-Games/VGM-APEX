package net.vpg.apex

import android.content.Context
import net.vpg.apex.player.ApexTrack
import net.vpg.vjson.parser.JSONParser.toJSON

object DataLoader {
    fun loadData(context: Context) {
        loadTracksList(context)
    }

    fun loadTracksList(context: Context) {
        context.assets
            .open("tracks.json")
            .toJSON()
            .toArray()
            .forEach { ApexTrack(it.toObject()) }
    }
}