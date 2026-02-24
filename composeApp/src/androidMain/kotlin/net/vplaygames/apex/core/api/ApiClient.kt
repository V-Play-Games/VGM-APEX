package net.vplaygames.apex.core.api

import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import net.vplaygames.apex.entities.*
import net.vplaygames.apex.entities.database.AlbumData
import net.vplaygames.apex.entities.database.TrackData
import net.vplaygames.apex.entities.database.UploaderData
import net.vplaygames.apex.entities.responses.AlbumResponse
import net.vplaygames.apex.entities.responses.HistoryResponse
import net.vplaygames.apex.entities.responses.UploaderResponse

object ApiClient {
    private const val BASE_URL = "https://api.vplaygames.net"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private suspend fun getAuthToken(): String? {
        return FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
    }

    private suspend fun HttpRequestBuilder.authorize() {
        getAuthToken()?.let { token ->
            this.header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    // Login
    suspend fun login(): Result<User> = runCatching {
        client.post("$BASE_URL/login") {
            authorize()
        }.body()
    }

    // History
    suspend fun getHistory(type: HistoryType, page: Int = 0, limit: Int = 50): Result<HistoryResponse> = runCatching {
        client.get("$BASE_URL/history/${type.path}") {
            authorize()
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun addToHistory(type: HistoryType, trackId: String): Result<String> = runCatching {
        client.post("$BASE_URL/history/${type.path}/$trackId") {
            authorize()
        }.body()
    }

    suspend fun clearHistory(type: HistoryType): Result<String> = runCatching {
        client.delete("$BASE_URL/history/${type.path}") {
            authorize()
        }.body()
    }

    suspend fun removeFromHistory(type: HistoryType, trackId: String, timestamp: Long): Result<String> = runCatching {
        client.delete("$BASE_URL/history/${type.path}/item") {
            authorize()
            parameter("trackId", trackId)
            parameter("timestamp", timestamp)
        }.body()
    }

    // Album
    suspend fun getAlbum(id: String): Result<ApexAlbum> = runCatching {
        client.get("$BASE_URL/album/$id") {
            authorize()
        }.body<AlbumResponse>().toApexAlbum()
    }

    suspend fun AlbumData.toApexAlbum() = getAlbum(id)

    // Track
    suspend fun getTrack(id: String): Result<ApexTrack> = runCatching {
        client.get("$BASE_URL/track/$id") {
            authorize()
        }.body()
    }

    suspend fun TrackData.toApexTrack() = getTrack(id)

    // Uploader
    suspend fun getUploader(id: String): Result<ApexUploader> = runCatching {
        client.get("$BASE_URL/uploader/$id") {
            authorize()
        }.body<UploaderResponse>().toApexUploader()
    }

    suspend fun UploaderData.toApexUploader() = getUploader(id)

    // Search
    suspend fun search(query: String): Result<List<SearchResultEntry>> = runCatching {
        client.get("$BASE_URL/search") {
            authorize()
            parameter("query", query)
        }.body()
    }
}
