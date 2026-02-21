package net.vplaygames.apex.route

import com.google.firebase.auth.FirebaseAuth
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.vplaygames.apex.Database
import net.vplaygames.apex.Database.authenticateUser
import net.vplaygames.apex.Database.users
import net.vplaygames.apex.entities.User

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
                    users.insertOne(newUser)
                }
            }

            call.respond(user.copy(
                playHistory = user.playHistory.take(10),
                searchHistory = user.searchHistory.take(10)
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to process login: ${e.message}")
        }
    }
}

