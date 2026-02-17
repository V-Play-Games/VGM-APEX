package net.vpg.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    val history: List<HistoryElement>,
    val page: Int,
    val limit: Int,
    val totalItems: Int,
    val hasMore: Boolean
)
