package net.vplaygames.apex.entities.database

import kotlinx.serialization.Serializable

@Serializable
sealed interface SearchResultValue {
    val name: String
}