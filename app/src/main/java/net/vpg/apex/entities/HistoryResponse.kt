package net.vpg.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class HistoryResponse(
    val playHistory: List<HistoryElement>? = null,
    val searchHistory: List<HistoryElement>? = null,
    val page: Int,
    val limit: Int,
    val totalItems: Int,
    val hasMore: Boolean
)

