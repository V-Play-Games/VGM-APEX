/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.apex.player

import androidx.media3.common.MediaItem
import net.vpg.vjson.value.JSONObject
import java.io.File

data class ApexTrack(
    val id: String,
    val title: String,
    private val uploaderId: String,
    private val albumId: String,
    val frameLength: Int,
    val loopStart: Int,
    val loopEnd: Int,
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
        data.getInt("frameLength"),
        data.getInt("loopStart"),
        data.getInt("loopEnd"),
        data.getString("dateAdded"),
        data.getString("url")
    )

    init {
        TRACKS_DB.put(id, this)
    }

    val album by lazy { ApexAlbum.ALBUMS_DB[albumId]!! }
    val uploader by lazy { ApexUploader.UPLOADERS_DB[uploaderId]!! }

    fun toMediaItem(cacheDir: File) = MediaItem.fromUri(
        downloadedFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: cacheFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: url
    )

    fun cacheFile(cacheDir: File) = File(cacheDir, "$id.wav")

    fun downloadedFile(cacheDir: File) = File(cacheDir, "$id.ogg")
}
