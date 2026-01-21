import org.jsoup.Jsoup

const val khBaseUrl = "https://downloads.khinsider.com"
const val khGamesPageUrl = "$khBaseUrl/game-soundtracks?page="

suspend fun main() {
    val pageCount = Jsoup.connect(khGamesPageUrl + "1")
        .get()
        .getElementsByClass("counter")
        .text()
        .split(" of ")
        .last()
        .toInt()
    (1..1)
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
        .executeScrapeTask("Scrape Game Pages") { url ->
            url to "apex/khinsider/album/${url.substringAfterLast("/")}.html"
        }
        .mapToResult()
        .executeTask("Parse Game Pages") { Jsoup.parse(it) }
        .mapToResult()
        .executeTask("Extract Album Details") { doc ->
            val pageContent = doc.getElementById("pageContent")
            val name = pageContent
                ?.getElementsByTag("h2")
                ?.first()
                ?.text()
            if (name == null)
                return@executeTask null
            val dateAdded = pageContent.getElementsByTag("b")
                .map { it.text() }
                .first { it.matches(Regex("[A-Z][a-z]{2} \\d{1,2}(st|nd|th), \\d{4}")) }
            val uploader = pageContent.getElementsByTag("a")
                .map { it.text() to it.attr("href") }
                .firstOrNull { (_, link) -> link.startsWith("/forums/index.php?members/") }
                ?.first ?: ""
            val albumArtUrl = pageContent.getElementsByClass("albumImage")
                .firstOrNull()
                ?.getElementsByTag("img")
                ?.firstOrNull()
                ?.attr("src")
                ?.let { if (it.startsWith("http")) it else khBaseUrl + it }
            return@executeTask uploader to AlbumData(
                id = "kh-${name.hashCode()}-${dateAdded.hashCode()}",
                name = name,
                dateAdded = dateAdded,
                albumArtUrl = albumArtUrl,
                trackIds = mutableListOf()
            )
        }
        .filterSuccessful()
        .filter { (_, album) -> album != null }
        .executeTask("Extract Song Links") { (doc, _) ->
            doc.getElementsByClass("playlistDownloadSong")
                .flatMap { it.getElementsByTag("a") }
                .map { khBaseUrl + it.attr("href") }
        }
        .filterSuccessful()
        .flatMap { (task, list) -> list.map { task.result!! to it } }
        .take(1000)
        .executeScrapeTask("Fetch Song Data") { (task, url) ->
            url to "apex/khinsider/song/${task.second.id}-${url.substringAfterLast("/").hashCode()}.html"
        }
        .filterSuccessful()
        .executeTask("Parse Song Pages") { (task, file) ->
            val pageContent = Jsoup.parse(file).getElementById("pageContent")
            val title = pageContent?.getElementsByTag("p")
                ?.text()
                ?.lines()
                ?.firstOrNull { it.contains("Song name") }
                ?.substringAfter(": ")
            if (title == null)
                throw IllegalStateException("No title found for song page: ${task.second}")
            val url = pageContent.getElementsByTag("audio")
                .firstOrNull()
                ?.attr("src")
                ?.let { if (it.startsWith("http")) it else khBaseUrl + it }
            if (url == null)
                throw IllegalStateException("No url found for song page: ${task.second}")
            val uploader = task.first.first
            val albumData = task.first.second
            return@executeTask albumData to TrackData(
                id = "kh-${title.hashCode()}-${albumData.id.replace("kh-", "")}",
                title = title,
                uploaderId = "kh-${uploader}",
                albumId = albumData.id,
                dateAdded = albumData.dateAdded,
                url = url
            )
        }
        .mapToResult()
        .filterNotNull()
        .onEach { (_, v) -> println(v) }
        .groupBy { it.first }
        .mapValues { (_, list) -> list.map { (_, track) -> track } }
        .onEach { (_, v) -> println(v) }
        .onEach { (album, tracks) -> album.trackIds.addAll(tracks.map { it.id }) }
}
