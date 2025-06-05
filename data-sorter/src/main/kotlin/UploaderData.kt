import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class UploaderData(
    val id: String,
    val name: String,
    val trackIds: List<String>
) : SerializableObject {
    override fun toObject() = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("trackIds", trackIds)
}
