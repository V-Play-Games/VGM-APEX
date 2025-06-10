import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class TrackData(
    val id: String,
    val title: String,
    val uploaderId: String,
    val albumId: String,
    val frameLength: Int,
    val loopStart: Int,
    val loopEnd: Int,
    val sampleRate: Int,
    val dateAdded: String,
    val url: String
) : SerializableObject {
    override fun toObject() = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("uploaderId", uploaderId)
        .put("albumId", albumId)
        .put("frameLength", frameLength)
        .put("loopStart", loopStart)
        .put("loopEnd", loopEnd)
        .put("sampleRate", sampleRate)
        .put("dateAdded", dateAdded)
        .put("url", url)
}
