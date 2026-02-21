package net.vplaygames.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class ApexUploader(
    val id: String,
    override val name: String,
    override val tracks: List<ApexTrack>
) : ApexTrackContext
