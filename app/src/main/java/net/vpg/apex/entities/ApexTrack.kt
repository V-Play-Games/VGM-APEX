package net.vpg.apex.entities

import kotlinx.serialization.Serializable
import net.vpg.vjson.value.JSONObject
import java.io.File

@Serializable
data class ApexTrack(
    val id: String,
    val title: String,
    private val uploaderId: String,
    private val albumId: String,
    val frameLength: Int,
    val loopStart: Int,
    val loopEnd: Int,
    val sampleRate: Int,
    val dateAdded: String,
    val url: String
) {
    companion object {
        val TRACKS_DB = mutableMapOf<String, ApexTrack>()
        val EMPTY = ApexTrack("", "", "", "", 0, 0, 0, 0, "", "")
    }

    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("title"),
        data.getString("uploaderId"),
        data.getString("albumId"),
        data.getInt("frameLength"),
        data.getInt("loopStart"),
        data.getInt("loopEnd"),
        data.getInt("sampleRate"),
        data.getString("dateAdded"),
        data.getString("url")
    )

    init {
        TRACKS_DB.put(id, this)
    }

    val album by lazy { ApexAlbum.ALBUMS_DB[albumId]!! }
    val uploader by lazy { ApexUploader.UPLOADERS_DB[uploaderId]!! }

    fun cacheFile(cacheDir: File) = File(cacheDir, "$id.wav")

    fun downloadedFile(cacheDir: File) = File(cacheDir, "$id.ogg")
}
