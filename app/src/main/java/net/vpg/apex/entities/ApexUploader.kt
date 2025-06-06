package net.vpg.apex.entities

import net.vpg.vjson.value.JSONObject

class ApexUploader(
    val id: String,
    val name: String,
    val trackIds: List<String>
) {
    companion object {
        val UPLOADERS_DB = mutableMapOf<String, ApexUploader>()
        val EMPTY = ApexUploader("", "", listOf(""))
    }

    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getArray("trackIds").map { it.toString() }
    )

    init {
        UPLOADERS_DB.put(id, this)
    }

    val tracks: List<ApexTrack> by lazy { trackIds.mapNotNull { ApexTrack.TRACKS_DB[it] } }
}