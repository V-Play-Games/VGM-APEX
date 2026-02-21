package net.vplaygames.apex.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vplaygames.apex.Database
import net.vplaygames.apex.entities.responses.UploaderResponse

fun Routing.uploaderRoute() {
    get("/uploader/{id}") {
        val id = call.parameters["id"]
            ?.takeIf { it.isNotEmpty() }
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Uploader ID is required")

        val uploader = Database.getUploaderById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Uploader not found")

        val tracks = Database.getTracksByUploaderId(uploader.id)
        val albums = Database.getAlbumByIds(tracks.map { it.albumId }.distinct())

        call.respond(
            UploaderResponse(
                uploader.id,
                uploader.name,
                albums,
                tracks
            )
        )
    }
}
