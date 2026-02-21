package net.vplaygames.apex.entities

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.database.AlbumData
import net.vplaygames.apex.entities.database.UploaderData

@Serializable
data class ApexTrack(
    val id: String,
    val title: String,
    val uploader: UploaderData,
    val album: AlbumData,
    val loopStart: Int,
    val loopEnd: Int,
    val sampleRate: Int,
    val dateAdded: String,
    val url: String
)
