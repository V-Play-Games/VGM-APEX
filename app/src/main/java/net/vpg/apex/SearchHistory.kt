package net.vpg.apex

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import net.vpg.apex.player.ApexTrack
import java.io.File
import java.util.logging.Logger

class SearchHistory(context: Context) {
    companion object {
        private val LOGGER = Logger.getLogger(SearchHistory::class.java.name)
    }

    private val historyFile = File(context.cacheDir, "history.txt")
        .also { if (!it.exists()) it.writeText("[]") }
    private val _history = historyFile.readLines()
        .mapNotNull { ApexTrack.TRACKS_DB[it] }
        .toMutableStateList()

    fun addTrack(track: ApexTrack) {
        _history.remove(track)
        _history.add(track)
        writeFile()
        LOGGER.info("Added ${track.name} (id=${track.id} to search history")
    }

    fun removeTrack(track: ApexTrack) {
        _history.remove(track)
        writeFile()
        LOGGER.info("Removed ${track.name} (id=${track.id} from search history")
    }

    fun getTracks() = _history.reversed()

    private fun writeFile() {
        historyFile.writeText(_history.joinToString("\n") { it.id })
    }
}