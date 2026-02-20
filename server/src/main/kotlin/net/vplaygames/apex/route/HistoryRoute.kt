package net.vplaygames.apex.route

import com.mongodb.client.model.Updates
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vplaygames.apex.Database.findUser
import net.vplaygames.apex.Database.updateUser
import net.vplaygames.apex.entity.HistoryElement
import net.vplaygames.apex.entity.User

fun Routing.historyRoute() {
    historyRoutes("play", User::playHistory)
    historyRoutes("search", User::searchHistory)
}

private fun Routing.historyRoutes(type: String, getHistory: User.() -> List<HistoryElement>) {
    val fieldName = "${type}History"

    get("/history/$type") {
        val page = call.parameters["page"]?.toIntOrNull() ?: 0
        val limit = call.parameters["limit"]?.toIntOrNull()?.coerceAtMost(50) ?: 50

        try {
            val user = call.findUser() ?: return@get
            val history = user.getHistory()

            val totalItems = history.size
            val startIndex = page * limit
            val endIndex = minOf(startIndex + limit, totalItems)

            val paginatedHistory = if (startIndex >= totalItems)
                emptyList()
            else
                history.subList(startIndex, endIndex)

            call.respond(
                mapOf(
                    "history" to paginatedHistory,
                    "page" to page,
                    "limit" to limit,
                    "totalItems" to totalItems,
                    "hasMore" to (endIndex < totalItems)
                )
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch $type history: ${e.message}")
        }
    }

    delete("/history/$type") {
        try {
            call.updateUser(Updates.set(fieldName, emptyList<HistoryElement>())) ?: return@delete
            call.respond(HttpStatusCode.OK, "${type.replaceFirstChar { it.uppercase() }} history cleared")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to clear $type history: ${e.message}")
        }
    }

    post("/history/$type/{trackId}") {
        val trackId = call.parameters["trackId"]

        if (trackId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Track ID is required")
            return@post
        }

        try {
            call.updateUser(Updates.push(fieldName, HistoryElement(trackId))) ?: return@post
            call.respond(HttpStatusCode.OK, "Track added to $type history")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to update $type history: ${e.message}")
        }
    }

    delete("/history/$type/item") {
        val trackId = call.request.queryParameters["trackId"]
        val timestamp = call.request.queryParameters["timestamp"]?.toLongOrNull()

        if (trackId.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Track ID is required")
            return@delete
        }

        if (timestamp == null) {
            call.respond(HttpStatusCode.BadRequest, "Valid timestamp is required")
            return@delete
        }

        try {
            call.updateUser(
                Updates.pull(fieldName, HistoryElement(trackId, timestamp))
            ) ?: return@delete
            call.respond(HttpStatusCode.OK, "Item removed from $type history")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to remove item from $type history: ${e.message}")
        }
    }
}
