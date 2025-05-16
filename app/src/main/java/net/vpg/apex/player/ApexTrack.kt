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
import net.vpg.vjson.value.JSONArray
import net.vpg.vjson.value.JSONObject
import javax.inject.Inject

data class ApexTrack(
    val id: String,
    val name: String,
    val category: String,
    val frameLength: Int,
    val loopStart: Int,
    val loopEnd: Int,
    val url: String
) {
    constructor(data: JSONObject) : this(
        data.getString("id"),
        data.getString("name"),
        data.getString("category"),
        data.getInt("frameLength"),
        data.getInt("loopStart"),
        data.getInt("loopEnd"),
        data.getString("url")
    )

    fun toMediaItem() = MediaItem.fromUri(url)

    init {
        println("Loaded Track Info for ID: $id")
    }
}
