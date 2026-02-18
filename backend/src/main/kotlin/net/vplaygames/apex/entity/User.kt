package net.vplaygames.apex.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String,
    val playHistory: List<HistoryElement> = emptyList(),
    val searchHistory: List<HistoryElement> = emptyList()
)
