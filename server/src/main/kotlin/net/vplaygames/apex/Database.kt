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
import kotlinx.serialization.json.Json
import net.vplaygames.apex.entities.User
import net.vplaygames.apex.entities.database.AlbumData
import net.vplaygames.apex.entities.database.TrackData
import net.vplaygames.apex.entities.database.UploaderData
import org.bson.conversions.Bson
import java.io.File

object Database {
    private val db: MongoDatabase = MongoClient.create(System.getenv("MONGODB_URI"))
        .getDatabase("apex")
    val users = db.getCollection<User>("users")
    val tracks =
//        db.getCollection<TrackData>("tracks")
        File("server/tracks.json").readText().let { Json.decodeFromString<List<TrackData>>(it) }
    val albums =
//        db.getCollection<AlbumData>("albums")
        File("server/albums.json").readText().let { Json.decodeFromString<List<AlbumData>>(it) }
    val uploaders =
//        db.getCollection<UploaderData>("uploaders")
        File("server/uploaders.json").readText().let { Json.decodeFromString<List<UploaderData>>(it) }

    suspend fun findUserByUid(uid: String) =
        users.find(Filters.eq("uid", uid)).firstOrNull()

    suspend fun updateUserByUid(uid: String, update: Bson, options: UpdateOptions = UpdateOptions()) =
        users.updateOne(Filters.eq("uid", uid), update, options).takeIf { it.matchedCount > 0L }

    suspend fun getTrackById(id: String) =
//        tracks.find(Filters.eq("id", id)).firstOrNull()
        tracks.find { it.id == id }

    suspend fun getTracksByAlbumId(albumId: String) =
//        tracks.find(Filters.eq("albumId", albumId)).toList()
        tracks.filter { it.albumId == albumId }

    suspend fun getTracksByUploaderId(uploaderId: String) =
//        tracks.find(Filters.eq("uploaderId", uploaderId)).toList()
        tracks.filter { it.uploaderId == uploaderId }

    suspend fun getAlbumById(id: String) =
//        albums.find(Filters.eq("id", id)).firstOrNull()
        albums.find { it.id == id }

    suspend fun getAlbumByIds(ids: List<String>) =
//        albums.find(Filters.`in`("id", ids)).toList()
        albums.filter { it.id in ids }

    suspend fun getUploaderById(id: String) =
//        uploaders.find(Filters.eq("id", id)).firstOrNull()
        uploaders.find { it.id == id }

    suspend fun getUploaderByIds(ids: List<String>) =
//        uploaders.find(Filters.`in`("id", ids)).toList()
        uploaders.filter { it.id in ids }

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
