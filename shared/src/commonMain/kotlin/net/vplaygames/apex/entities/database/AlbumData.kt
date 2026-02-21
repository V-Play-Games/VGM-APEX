package net.vplaygames.apex.entities.database

import kotlinx.serialization.Serializable
import net.vplaygames.apex.entities.SearchResultValue

@Serializable
abstract class AlbumData : SearchResultValue {
    abstract val id: String
    abstract override val name: String
    abstract val albumArtUrl: String?
    abstract val dateAdded: String
}
