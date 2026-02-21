package net.vplaygames.apex.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vplaygames.apex.Database
import net.vplaygames.apex.entities.responses.AlbumResponse

fun Routing.albumRoute() {
    get("/album/{id}") {
        val id = call.parameters["id"]
            ?.takeIf { it.isNotEmpty() }
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Album ID is required")

        val album = Database.getAlbumById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Album not found")

        val tracks = Database.getTracksByAlbumId(album.id)
        val uploaders = Database.getUploaderByIds(tracks.map { it.uploaderId }.distinct())

        call.respond(
            AlbumResponse(
                album.id,
                album.name,
                album.albumArtUrl,
                album.dateAdded,
                uploaders,
                tracks
            )
        )
    }
}
