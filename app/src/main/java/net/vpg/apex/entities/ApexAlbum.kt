package net.vpg.apex.entities

import net.vpg.vjson.value.JSONObject

data class ApexAlbum(
    val id: String,
    val name: String,
    val albumArtUrl: String?,
    val dateAdded: String,
    private val trackIds: List<String>
) {
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

    val tracks: List<ApexTrack> by lazy { trackIds.mapNotNull { ApexTrack.TRACKS_DB[it] } }
}