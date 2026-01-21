package data

import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class AlbumData(
    val id: String,
    val name: String,
    val albumArtUrl: String?,
    val dateAdded: String,
    val trackIds: List<String>
) : SerializableObject {
    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getString("albumArtUrl"),
        data.getString("dateAdded"),
        data.getArray("trackIds").map { it.toString() }
    )

    override fun toObject() = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("albumArtUrl", albumArtUrl)
        .put("dateAdded", dateAdded)
        .put("trackIds", trackIds)
}