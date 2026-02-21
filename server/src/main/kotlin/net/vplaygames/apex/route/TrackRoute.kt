package net.vplaygames.apex.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vplaygames.apex.Database

fun Routing.trackRoute() {
    get("/track/{id}") {
        val id = call.parameters["id"]
            ?.takeIf { it.isNotBlank() }
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Track ID is required")

        val track = Database.getTrackById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "Track not found")
        val uploader = Database.getUploaderById(track.uploaderId)!!
        val album = Database.getAlbumById(track.albumId)!!

        call.respond(track.toApexTrack(uploader, album))
    }
}
