package net.vpg.apex.entities

import kotlinx.serialization.Serializable
import net.vpg.vjson.value.JSONObject

@Serializable
data class ApexAlbum(
    val id: String,
    override val name: String,
    val albumArtUrl: String?,
    val dateAdded: String,
    val trackIds: List<String>
) : ApexTrackContext {
    companion object {
        val ALBUMS_DB = mutableMapOf<String, ApexAlbum>()
        val EMPTY = ApexAlbum("", "", "", "", listOf(""))
    }

    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getString("albumArtUrl"),
        data.getString("dateAdded"),
        data.getArray("trackIds").map { it.toString() }
    )

    init {
        ALBUMS_DB.put(id, this)
    }

    override val tracks: List<ApexTrack> by lazy { trackIds.mapNotNull { ApexTrack.TRACKS_DB[it] } }
}