package net.vplaygames.apex.route

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.vplaygames.apex.Database.albums
import net.vplaygames.apex.Database.tracks
import net.vplaygames.apex.Database.uploaders
import net.vplaygames.apex.entities.SearchResultEntry
import net.vplaygames.apex.entities.SearchResultType
import java.util.regex.Pattern

fun Routing.searchRoute() {
    get("/search") {
        val query = call.request.queryParameters["query"]
            ?.takeIf { it.isNotBlank() }
            ?: return@get call.respond(emptyList<SearchResultEntry>())

        val pattern = Pattern.quote(query)
//        val regex = Filters.regex("name", pattern, "i") // "i" for case-insensitive
        val regex = pattern.toRegex()

        coroutineScope {
            mapOf(
                tracks to SearchResultType.TRACK,
                albums to SearchResultType.ALBUM,
                uploaders to SearchResultType.UPLOADER
            ).map { (collection, type) ->
                async {
//                    collection.find(regex).toList().map { SearchResultEntry(type, it) }
                    collection.filter { regex.find(it.name) != null }.map { SearchResultEntry(type, it) }
                }
            }.awaitAll().flatten().let { call.respond(it) }
        }
    }
}
