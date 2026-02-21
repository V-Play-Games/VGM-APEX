package net.vplaygames.apex.entities.responses

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.ApexAlbum
import net.vplaygames.apex.entities.database.AlbumData
import net.vplaygames.apex.entities.database.TrackData
import net.vplaygames.apex.entities.database.UploaderData

@Serializable
data class AlbumResponse(
    override val id: String,
    override val name: String,
    override val albumArtUrl: String?,
    override val dateAdded: String,
    val uploaders: List<UploaderData>,
    val tracks: List<TrackData>
) : AlbumData() {
    fun toApexAlbum(): ApexAlbum {
        val uploaderMap = uploaders.associateBy { it.id }
        val apexTracks = tracks.map { it.toApexTrack(uploaderMap[it.uploaderId]!!, this) }
        return ApexAlbum(
            id = id,
            name = name,
            albumArtUrl = albumArtUrl,
            dateAdded = dateAdded,
            tracks = apexTracks
        )
    }
}
