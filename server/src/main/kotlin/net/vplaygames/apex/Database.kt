package net.vplaygames.apex

import com.google.firebase.auth.FirebaseAuth
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.flow.firstOrNull
import net.vplaygames.apex.entities.User
import org.bson.conversions.Bson

object Database {
    private val client = MongoClient.create(
        System.getenv("MONGODB_URI") ?: "mongodb://localhost:27017"
    )

    val db: MongoDatabase = client.getDatabase("apex")

    suspend fun findUserByUid(uid: String) = db.getCollection<User>("users")
        .find(Filters.eq("uid", uid))
        .firstOrNull()

    suspend fun updateUserByUid(uid: String, update: Bson, options: UpdateOptions = UpdateOptions()) =
        db.getCollection<User>("users")
            .updateOne(Filters.eq("uid", uid), update, options)
            .takeIf { it.matchedCount > 0L }

    suspend fun ApplicationCall.findUser(): User? {
        val uid = authenticateUser() ?: return null

        return findUserByUid(uid) ?: run {
            respond(HttpStatusCode.NotFound, "User not found")
            null
        }
    }

    suspend fun ApplicationCall.updateUser(update: Bson, options: UpdateOptions = UpdateOptions()): UpdateResult? {
        val uid = authenticateUser() ?: return null

        return updateUserByUid(uid, update, options) ?: run {
            respond(HttpStatusCode.NotFound, "User not found")
            null
        }
    }

    suspend fun ApplicationCall.authenticateUser(): String? {
        val token = request.header("Authorization")?.removePrefix("Bearer ")
        if (token == null) {
            respond(HttpStatusCode.Unauthorized, "Missing authorization token")
            return null
        }

        return try {
            FirebaseAuth.getInstance().verifyIdToken(token).uid
        } catch (_: Exception) {
            respond(HttpStatusCode.Unauthorized, "Invalid token")
            null
        }
    }
}
