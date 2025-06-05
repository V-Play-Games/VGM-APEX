import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONArray
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
    val titleRegex = "^(([A-Za-z\\- ]+|[A-Za-z\\-0-9]+) \\d{2,3}[a-z]?) (.+)$".toRegex()
    val date = "2025-06-05"
    val idToNameMap = mapOf(
        "B2W2" to "Pokemon Black 2 & White 2",
        "BR" to "Pokemon Battle Revolution",
        "BW" to "Pokemon Black & White",
        "Conquest" to "Pokemon Conquest",
        "DPP" to "Pokemon Diamond, Pearl, and Platinum",
        "Dash" to "Pokemon Dash",
        "FRLG" to "Pokemon FireRed & LeafGreen",
        "GSC" to "Pokemon Gold, Silver, and Crystal",
        "HGSS" to "Pokemon HeartGold & SoulSilver",
        "HOME" to "Pokemon Home",
        "ORAS" to "Pokemon Omega Ruby & Alpha Sapphire",
        "PB" to "Pokemon Pinball",
        "PIC" to "Pokemon Picross",
        "PLA" to "Pokemon Legends: Arceus",
        "PM" to "Pokemon Masters EX",
        "Ranger" to "Pokemon Ranger",
        "Ranger-SOA" to "Pokemon Ranger: Shadows of Almia",
        "RBY" to "Pokemon Red, Blue & Yellow",
        "RSE" to "Pokemon Rube, Sapphire & Emerald",
        "SM" to "Pokemon Sun & Moon",
        "SwSh" to "Pokemon Sword & Shield",
        "TA" to "Pokemon Typing Adventure",
        "TCG" to "Pokemon Trading Card Game",
        "XD" to "Pokemon XD: Gale of Darkness",
        "XY" to "Pokemon X & Y",
    )

    val tracks = File("data-sorter/raw-tracks.json")
        .toJSON()
        .toArray()
        .map { RawTrackData(it.toObject()) }
        .map {
            val encodedFileName = URLEncoder.encode(it.id, StandardCharsets.UTF_8.name()).replace("+", "%20")
            TrackData(
                id = titleRegex.find(it.name)?.groupValues[1]?.replace(" ", "") ?: it.id,
                title = titleRegex.find(it.name)?.groupValues[3] ?: it.name,
                uploaderId = "V Play Games",
                albumId = it.category,
                frameLength = it.frameLength,
                loopStart = it.loopStart,
                loopEnd = it.loopEnd,
                dateAdded = date,
                url = "https://github.com/VGM-Apex/${it.category}/raw/main/$encodedFileName.ogg"
            )
        }.groupBy { it.id }
        // Replace Duplicate IDs
        .map { (id, trackList) ->
            if (trackList.size == 1)
                trackList
            else
                trackList.mapIndexed { index, track -> track.copy(id = "${id}${(index + 'a'.code).toChar()}") }
        }.flatten()

    val albums = tracks.groupBy { it.albumId }.map { (albumId, albumTracks) ->
        val albumUrl = "https://github.com/VGM-Apex/apex-image/raw/main/$albumId.png"
        val responseCode = (URI.create(albumUrl).toURL().openConnection() as HttpURLConnection)
            .also { it.requestMethod = "GET" }
            .let {
                try {
                    it.responseCode
                } finally {
                    it.disconnect()
                }
            }
        val url = if (responseCode / 100 == 2) albumUrl else null
        println("Album $albumId: $url, ${albumTracks.size} tracks")
        AlbumData(
            id = albumId,
            name = idToNameMap[albumId]!!,
            albumArtUrl = url,
            dateAdded = date,
            trackIds = albumTracks.map { it.id }
        )
    }

    val uploaderData = UploaderData(
        id = "vplaygames",
        name = "V Play Games",
        trackIds = tracks.map { it.id }
    )
    File("data-sorter/tracks.json").writeText(JSONArray().addAll(tracks).toPrettyString())
    File("data-sorter/albums.json").writeText(JSONArray().addAll(albums).toPrettyString())
    File("data-sorter/uploaders.json").writeText(JSONArray().add(uploaderData).toPrettyString())
}
