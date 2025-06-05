package net.vpg.apex

import android.content.Context
import net.vpg.apex.player.ApexAlbum
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.player.ApexUploader
import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONObject

object DataLoader {
    fun loadData(context: Context) {
        loadTracksList(context)
    }

    fun loadTracksList(context: Context) {
        mapOf<String, (JSONObject) -> Unit>(
            "tracks" to { ApexTrack(it) },
            "albums" to { ApexAlbum(it) },
            "uploaders" to { ApexUploader(it) }
        ).forEach { (type, constructor) ->
            context.assets
                .open("$type.json")
                .toJSON()
                .toArray()
                .forEach { constructor(it.toObject()) }
        }
    }
}