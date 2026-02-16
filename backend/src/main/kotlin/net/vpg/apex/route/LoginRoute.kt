package net.vpg.apex.route

import com.google.firebase.auth.FirebaseAuth
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vpg.apex.Database
import net.vpg.apex.Database.authenticateUser
import net.vpg.apex.entity.User

fun Routing.loginRoute() {
    post("/login") {
        val uid = call.authenticateUser() ?: return@post

        try {
            val user = Database.findUserByUid(uid) ?: run {
                val firebaseUser = FirebaseAuth.getInstance().getUser(uid)
                User(
                    uid = uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName ?: "User${uid.take(6)}",
                    photoUrl = firebaseUser.photoUrl ?: "",
                ).also { newUser ->
                    Database.db.getCollection<User>("users").insertOne(newUser)
                }
            }

            call.respond(user)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to process login: ${e.message}")
        }
    }
}

