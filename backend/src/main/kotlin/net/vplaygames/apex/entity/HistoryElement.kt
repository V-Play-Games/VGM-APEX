package net.vplaygames.apex.entity

import kotlinx.serialization.Serializable

@Serializable
data class HistoryElement(
    val trackId: String,
    val timestamp: Long
) {
    constructor(trackId: String) : this(trackId, System.currentTimeMillis())
}
