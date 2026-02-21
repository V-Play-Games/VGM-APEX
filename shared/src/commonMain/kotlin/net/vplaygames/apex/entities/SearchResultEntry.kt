package net.vplaygames.apex.entities

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultEntry(val type: SearchResultType, val result: SearchResultValue)

enum class SearchResultType { ALBUM, TRACK, UPLOADER }

interface SearchResultValue {
    val name: String
}
