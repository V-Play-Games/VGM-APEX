data class AlbumData(
    val id: String,
    val name: String,
    val albumArtUrl: String,
    val dateAdded: String,
    val dataReleased: String,
    val trackIds: List<String>
)