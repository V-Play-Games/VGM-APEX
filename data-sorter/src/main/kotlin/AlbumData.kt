import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class AlbumData(
    val id: String,
    val name: String,
    val albumArtUrl: String?,
    val dateAdded: String,
    val trackIds: List<String>
) : SerializableObject {
    override fun toObject() = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("albumArtUrl", albumArtUrl)
        .put("dateAdded", dateAdded)
        .put("trackIds", trackIds)
}
