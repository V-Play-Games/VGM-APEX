package data

import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject

data class UploaderData(
    val id: String,
    val name: String
) : SerializableObject {
    override fun toObject() = JSONObject()
        .put("id", id)
        .put("name", name)
}
