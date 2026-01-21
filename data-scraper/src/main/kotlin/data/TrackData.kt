package data

import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class TrackData(
    val id: String,
    val title: String,
    val uploaderId: String,
    val albumId: String,
    val loopStart: Int = -1,
    val loopEnd: Int = -1,
    val sampleRate: Int = -1,
    val dateAdded: String,
    val url: String
) : SerializableObject {
    constructor(obj: JSONObject) : this(
        id = obj.getString("id"),
        title = obj.getString("title"),
        uploaderId = obj.getString("uploaderId"),
        albumId = obj.getString("albumId"),
        loopStart = obj.getInt("loopStart"),
        loopEnd = obj.getInt("loopEnd"),
        sampleRate = obj.getInt("sampleRate"),
        dateAdded = obj.getString("dateAdded"),
        url = obj.getString("url")
    )

    override fun toObject() = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("uploaderId", uploaderId)
        .put("albumId", albumId)
        .put("loopStart", loopStart)
        .put("loopEnd", loopEnd)
        .put("sampleRate", sampleRate)
        .put("dateAdded", dateAdded)
        .put("url", url)
}