package net.vplaygames.apex.entities.responses

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.ApexUploader
import net.vplaygames.apex.entities.database.AlbumData
import net.vplaygames.apex.entities.database.TrackData
import net.vplaygames.apex.entities.database.UploaderData

@Serializable
class UploaderResponse(
    override val id: String,
    override val name: String,
    val albums: List<AlbumData>,
    val tracks: List<TrackData>
) : UploaderData() {
    fun toApexUploader(): ApexUploader {
        val albumMap = albums.associateBy { it.id }
        val apexTracks = tracks.map { it.toApexTrack(this, albumMap[it.albumId]!!) }
        return ApexUploader(
            id = id,
            name = name,
            tracks = apexTracks
        )
    }
}