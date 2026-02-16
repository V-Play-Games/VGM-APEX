package net.vpg.apex.core.api

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
import net.vpg.apex.entities.HistoryResponse
import net.vpg.apex.entities.User

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // For Android emulator

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

    // Play History
    suspend fun getPlayHistory(page: Int = 0, limit: Int = 50): Result<HistoryResponse> = runCatching {
        client.get("$BASE_URL/history/play") {
            authorize()
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun addToPlayHistory(trackId: String): Result<String> = runCatching {
        client.post("$BASE_URL/history/play/$trackId") {
            authorize()
        }.body()
    }

    suspend fun clearPlayHistory(): Result<String> = runCatching {
        client.delete("$BASE_URL/history/play") {
            authorize()
        }.body()
    }

    suspend fun removeFromPlayHistory(trackId: String, timestamp: Long): Result<String> = runCatching {
        client.delete("$BASE_URL/history/play/item") {
            authorize()
            parameter("trackId", trackId)
            parameter("timestamp", timestamp)
        }.body()
    }

    // Search History
    suspend fun getSearchHistory(page: Int = 0, limit: Int = 50): Result<HistoryResponse> = runCatching {
        client.get("$BASE_URL/history/search") {
            authorize()
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun addToSearchHistory(trackId: String): Result<String> = runCatching {
        client.post("$BASE_URL/history/search/$trackId") {
            authorize()
        }.body()
    }

    suspend fun clearSearchHistory(): Result<String> = runCatching {
        client.delete("$BASE_URL/history/search") {
            authorize()
        }.body()
    }

    suspend fun removeFromSearchHistory(trackId: String, timestamp: Long): Result<String> = runCatching {
        client.delete("$BASE_URL/history/search/item") {
            authorize()
            parameter("trackId", trackId)
            parameter("timestamp", timestamp)
        }.body()
    }
}


