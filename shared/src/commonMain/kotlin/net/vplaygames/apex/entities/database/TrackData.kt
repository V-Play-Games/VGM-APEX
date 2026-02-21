package net.vplaygames.apex.entities.database

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.ApexTrack
import net.vplaygames.apex.entities.SearchResultValue

@Serializable
abstract class TrackData : SearchResultValue {
    abstract val id: String
    abstract val title: String
    abstract val uploaderId: String
    abstract val albumId: String
    abstract val loopStart: Int
    abstract val loopEnd: Int
    abstract val sampleRate: Int
    abstract val dateAdded: String
    abstract val url: String
    fun toApexTrack(uploader: UploaderData, album: AlbumData) = ApexTrack(
        id = id,
        title = title,
        uploader = uploader,
        album = album,
        loopStart = loopStart,
        loopEnd = loopEnd,
        sampleRate = sampleRate,
        dateAdded = dateAdded,
        url = url
    )
}
