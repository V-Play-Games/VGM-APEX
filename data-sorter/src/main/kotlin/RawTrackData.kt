import net.vpg.vjson.value.JSONObject

data class RawTrackData(
    val id: String,
    val name: String,
    val category: String,
    val loopStart: Int,
    val loopEnd: Int,
    val sampleRate: Int,
) {
    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getString("category"),
        data.getInt("loopStart"),
        data.getInt("loopEnd"),
        data.getInt("sampleRate")
    )
}