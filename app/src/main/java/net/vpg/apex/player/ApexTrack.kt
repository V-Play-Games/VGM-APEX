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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class ApexTrack(
    val id: String,
    val name: String,
    val category: String,
    val frameLength: Int,
    val loopStart: Int,
    val loopEnd: Int
) {
    companion object {
        val TRACKS_DB = mutableMapOf<String, ApexTrack>()
        val EMPTY = ApexTrack("", "", "", 0, 0, 0)
    }

    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getString("category"),
        data.getInt("frameLength"),
        data.getInt("loopStart"),
        data.getInt("loopEnd")
    )

    init {
        TRACKS_DB.put(id, this)
    }

    val url = "https://github.com/VGM-Apex/%s/raw/main/%s.%s"
        .format(
            category,
            URLEncoder.encode(id, StandardCharsets.UTF_8.name()).replace("+", "%20"),
            "ogg"
        )

    fun toMediaItem(cacheDir: File) = MediaItem.fromUri(
        downloadedFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: cacheFile(cacheDir).takeIf { it.exists() }?.toURI()?.toString()
            ?: url
    )

    fun cacheFile(cacheDir: File) = File(cacheDir, "$id.wav")

    fun downloadedFile(cacheDir: File) = File(cacheDir, "$id.ogg")
}
