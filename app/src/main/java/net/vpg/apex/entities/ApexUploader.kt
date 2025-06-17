package net.vpg.apex.entities

import kotlinx.serialization.Serializable
import net.vpg.vjson.value.JSONObject

@Serializable
data class ApexUploader(
    val id: String,
    override val name: String,
    val trackIds: List<String>
) : ApexTrackContext {
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

    override val tracks: List<ApexTrack> by lazy { trackIds.mapNotNull { ApexTrack.TRACKS_DB[it] } }
}