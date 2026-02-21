package net.vplaygames.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class ApexAlbum(
    val id: String,
    override val name: String,
    val albumArtUrl: String?,
    val dateAdded: String,
    override val tracks: List<ApexTrack>
) : ApexTrackContext
