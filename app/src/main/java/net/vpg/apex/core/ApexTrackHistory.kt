package net.vpg.apex.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.vpg.apex.core.api.ApiClient
import net.vpg.apex.core.api.HistoryType
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexTrackContext
import net.vpg.apex.entities.HistoryElement
import java.io.File

class SearchHistory(context: Context) : ApexTrackHistory(
    name = "Recent Searches",
    cacheFile = File(context.cacheDir, "search-history-cache.json"),
    historyType = HistoryType.SEARCH
) {
    override fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        removeTrack(track)
        super.addTrack(track, trackContext)
    }
}

class PlayHistory(context: Context) : ApexTrackHistory(
    name = "Recently Played",
    cacheFile = File(context.cacheDir, "play-history-cache.json"),
    historyType = HistoryType.PLAY
) {
    override fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        if (trackContext != this && tracks.firstOrNull() != track)
            super.addTrack(track, trackContext)
    }
}

open class ApexTrackHistory(
    override val name: String,
    private val cacheFile: File,
    private val historyType: HistoryType
) : ApexTrackContext {
    companion object {
        private val tag = ApexTrackHistory::class.java.canonicalName
        private val json = Json { ignoreUnknownKeys = true }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val historyElements = mutableListOf<HistoryElement>()
    override val tracks = mutableStateListOf<ApexTrack>()

    init {
        coroutineScope.launch { loadHistory() }
    }

    private suspend fun loadHistory() {
        ApiClient.getHistory(historyType, 0, 50).onSuccess { response ->
            historyElements.clear()
            historyElements.addAll(response.history)
            updateTracksFromHistory()
            writeCacheFile("Loaded ${response.history.size} items from API")
            return
        }.onFailure { e ->
            Log.w(tag, "Failed to fetch from API: ${e.message}, falling back to cache")
        }
        loadFromCacheFile()
    }

    private fun loadFromCacheFile() {
        cacheFile.takeIf { it.exists() }?.let { file ->
            try {
                val cachedHistory = json.decodeFromString<List<HistoryElement>>(file.readText())
                historyElements.clear()
                historyElements.addAll(cachedHistory)
                updateTracksFromHistory()
                Log.i(tag, "Loaded ${cachedHistory.size} items from cache: ${file.name}")
            } catch (e: Exception) {
                Log.e(tag, "Failed to load cache file: ${e.message}")
            }
        }
    }

    private fun updateTracksFromHistory() {
        tracks.clear()
        historyElements.mapNotNull { ApexTrack.TRACKS_DB[it.trackId] }.forEach { tracks.add(it) }
    }

    open fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        val timestamp = System.currentTimeMillis()
        historyElements.add(0, HistoryElement(track.id, timestamp))
        tracks.add(0, track)

        coroutineScope.launch {
            ApiClient.addToHistory(historyType, track.id)
                .onSuccess { Log.i(tag, "Added ${track.title} (id=${track.id}) to API") }
                .onFailure { e -> Log.w(tag, "Failed to add to API: ${e.message}") }
        }
        writeCacheFile("Added ${track.title} (id=${track.id}) to cache")
    }

    open fun removeTrack(track: ApexTrack) {
        val index = tracks.indexOf(track)
        if (index != -1) removeIndex(index)
    }

    open fun removeIndex(index: Int) {
        val element = historyElements.getOrNull(index) ?: return
        historyElements.removeAt(index)
        tracks.removeAt(index)

        coroutineScope.launch {
            ApiClient.removeFromHistory(historyType, element.trackId, element.timestamp)
                .onSuccess { Log.i(tag, "Removed item from API") }
                .onFailure { e -> Log.w(tag, "Failed to remove from API: ${e.message}") }
        }
        writeCacheFile("Removed item from cache")
    }

    open fun clear() {
        tracks.clear()
        historyElements.clear()

        coroutineScope.launch {
            ApiClient.clearHistory(historyType)
                .onSuccess { Log.i(tag, "Cleared history via API") }
                .onFailure { e -> Log.w(tag, "Failed to clear via API: ${e.message}") }
        }
        writeCacheFile("Cleared history cache")
    }

    fun refresh() {
        coroutineScope.launch { loadHistory() }
    }

    private fun writeCacheFile(log: String? = null) {
        try {
            cacheFile.writeText(json.encodeToString(historyElements))
            log?.let { Log.i(tag, it) }
        } catch (e: Exception) {
            Log.e(tag, "Failed to write cache file: ${e.message}")
        }
    }
}

