package net.vplaygames.apex.entities.responses

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.HistoryElement

@Serializable
data class HistoryResponse(
    val history: List<HistoryElement>,
    val page: Int,
    val limit: Int,
    val totalItems: Int,
    val hasMore: Boolean
)