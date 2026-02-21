import data.AlbumData
import data.TrackData
import data.UploaderData
import net.vpg.vjson.value.JSONArray.Companion.toJSON
import net.vpg.vjson.value.JSONObject
import net.vpg.vjson.value.SerializableObject
import org.jsoup.Jsoup
import java.io.File
import java.net.URLEncoder

const val khBaseUrl = "https://downloads.khinsider.com"
const val khGamesPageUrl = "$khBaseUrl/game-soundtracks?page="

suspend fun main() {
    val pageCount = Jsoup.connect(khGamesPageUrl + "1")
        .get()
        .getElementsByClass("counter")
        .text()
        .substringAfterLast(" of ")
        .toInt()
    val albumTrackMap = (1..pageCount)
        .toList()
        .executeScrapeTask("Scrape Game List Pages") { pageNum ->
            khGamesPageUrl + pageNum to "apex/khinsider/page/games-page-$pageNum.html"
        }
        .executeTask("Extract Game Links") { _, file ->
            Jsoup.parse(file)
                .getElementsByTag("a")
                .map { it.attr("href") }
                .filter { it.startsWith("/game-soundtracks/album/") }
                .map { khBaseUrl + it }
        }
        .mapToResult()
        .flatten()
        .distinct()
        .executeScrapeTask("Scrape Game Pages", true) { url ->
            url.substringBeforeLast('/') + "/" + URLEncoder.encode(url.substringAfterLast('/'), Charsets.UTF_8) to
                    "apex/khinsider/album/${url.substringAfterLast("/")}.html"
        }
        .mapToResult()
        .executeTask("Extract Album Details", { InterimAlbumData(it) }) { file ->
            val doc = Jsoup.parse(file)
            val pageContent = doc.getElementById("pageContent")
            val name = pageContent
                ?.getElementsByTag("h2")
                ?.first()
                ?.text()
            if (name == null)
                return@executeTask null
            val dateAdded = pageContent.getElementsByTag("b")
                .map { it.text() }
                .first { it.matches(Regex("[A-Z][a-z]{2} \\d{1,2}(st|nd|rd|th), \\d{4}")) }
            val uploader = pageContent.getElementsByTag("a")
                .map { it.text() to it.attr("href") }
                .firstOrNull { (_, link) -> link.startsWith("/forums/index.php?members/") }
                ?.let { (name, link) -> name + "-" + link.substringAfter("members").replace("/", "") }
                ?: "khinsider"
            val albumArtUrl = pageContent.getElementsByClass("albumImage")
                .firstOrNull()
                ?.getElementsByTag("img")
                ?.firstOrNull()
                ?.attr("src")
                ?.let { if (it.startsWith("http")) it else khBaseUrl + it }
            val id = "kh-${name.hashCode().toUInt()}-${dateAdded.hashCode().toUInt()}"
            val songLinks = doc.getElementsByClass("playlistDownloadSong")
                .flatMap { it.getElementsByTag("a") }
                .map { khBaseUrl + it.attr("href") }
            return@executeTask InterimAlbumData(
                albumData = AlbumData(
                    id = id,
                    name = name,
                    dateAdded = dateAdded,
                    albumArtUrl = albumArtUrl,
                ),
                uploader = uploader,
                songLinks = songLinks
            )
        }
        .mapToResult()
        .filterNotNull()
        .flatMap { data -> data.songLinks.map { data to it } }
        .executeScrapeTask("Fetch Song Data", true) { (task, url) ->
            url to "apex/khinsider/song/${task.albumData.id}-${url.substringAfterLast("/").hashCode().toUInt()}.html"
        }
        .filterSuccessful()
        .executeTask("Parse Song Pages", { TrackData(it) }) { task, file ->
            val pageContent = Jsoup.parse(file).getElementById("pageContent")
            val title = pageContent?.getElementsByTag("p")
                ?.map { it.text() }
                ?.firstOrNull { it.contains("Song name") }
                ?.substringAfter("Song name: ")
            if (title == null)
                throw IllegalStateException("No title found for song page: ${task.second}")
            val url = pageContent.getElementsByTag("audio")
                .firstOrNull()
                ?.attr("src")
                ?.let { if (it.startsWith("http")) it else khBaseUrl + it }
            if (url == null)
                throw IllegalStateException("No url found for song page: ${task.second}")
            val uploader = task.first.uploader
            val albumData = task.first.albumData
            return@executeTask TrackData(
                id = "kh-${title.hashCode().toUInt()}-${albumData.id.replace("kh-", "")}",
                title = title,
                uploaderId = "kh-${uploader}",
                albumId = albumData.id,
                dateAdded = albumData.dateAdded,
                url = url
            )
        }
        .filterSuccessful()
        .map { (task, trackData) -> task.first.albumData to trackData }
        .groupBy { it.first }
        .mapValues { (_, list) -> list.map { (_, track) -> track!! } }
    val uploaderList = albumTrackMap.values
        .flatten()
        .groupBy { it.uploaderId }
        .mapValues { (_, list) -> list.map { track -> track.id } }
        .map { (id, _) -> UploaderData(id = id, name = id.substringBeforeLast("-")) }
        .toJSON()
    val albumList = albumTrackMap.keys.toList().toJSON()
    val trackList = albumTrackMap.values.flatten().toJSON()

    File("data-scraper/khinsider-output").apply { mkdirs() }
        .also { dir ->
            File(dir, "uploaders.json").writeText(uploaderList.toPrettyString())
            File(dir, "albums.json").writeText(albumList.toPrettyString())
            File(dir, "tracks.json").writeText(trackList.toPrettyString())
        }
}

data class InterimAlbumData(
    val albumData: AlbumData,
    val uploader: String,
    val songLinks: List<String>
) : SerializableObject {
    constructor(obj: JSONObject) : this(
        albumData = AlbumData(obj.getObject("albumData")),
        uploader = obj.getString("uploader"),
        songLinks = obj.getArray("songLinks").map { it.toString() }
    )

    override fun toObject() = JSONObject()
        .put("albumData", albumData)
        .put("uploader", uploader)
        .put("songLinks", songLinks)
}
