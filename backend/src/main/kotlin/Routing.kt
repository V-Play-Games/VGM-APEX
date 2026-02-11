package net.vpg.apex

import com.google.firebase.auth.FirebaseAuth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() = routing {
    get("/") {
        call.respondText("Hello World!")
    }

    get("/protected") {
        val token = call.request.header("Authorization")?.removePrefix("Bearer ")
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
            val uid = decodedToken.uid
            call.respondText("Hello, user $uid")
        } catch (_: Exception) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}

