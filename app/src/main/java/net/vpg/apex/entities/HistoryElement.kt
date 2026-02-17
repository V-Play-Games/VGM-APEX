package net.vpg.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class HistoryElement(
    val trackId: String,
    val timestamp: Long
)
