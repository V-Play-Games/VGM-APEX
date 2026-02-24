package net.vplaygames.apex.entities

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.database.SearchResultValue

@Serializable
data class SearchResultEntry(val type: SearchResultType, val result: SearchResultValue)

enum class SearchResultType { ALBUM, TRACK, UPLOADER }

