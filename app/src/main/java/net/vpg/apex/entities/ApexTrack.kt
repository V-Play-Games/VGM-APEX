package net.vpg.apex.entities

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService.sendAddDownload
import kotlinx.serialization.Serializable
import net.vpg.apex.ApexDownloadService
import net.vpg.vjson.value.JSONObject

@Serializable
data class ApexTrack(
    val id: String,
    val title: String,
    private val uploaderId: String,
    private val albumId: String,
    val loopStart: Int,
    val loopEnd: Int,
    val sampleRate: Int,
    val dateAdded: String,
    val url: String
) {
    companion object {
        val TRACKS_DB = mutableMapOf<String, ApexTrack>()
        val EMPTY = ApexTrack("", "", "", "", 0, 0, 0, "", "")
    }

    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("title"),
        data.getString("uploaderId"),
        data.getString("albumId"),
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

    @OptIn(UnstableApi::class)
    fun download(context: Context) {
        val downloadRequest = DownloadRequest.Builder(id, url.toUri()).build()
        sendAddDownload(
            context,
            ApexDownloadService::class.java,
            downloadRequest,
            false
        )
    }
}
