package net.vpg.apex

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import net.vpg.apex.player.ApexTrack
import java.io.File
import java.util.logging.Logger

class SearchHistory(context: Context) : TrackHistory(context, "search-history.txt") {
    override fun addTrack(track: ApexTrack) {
        trackHistory.remove(track)
        super.addTrack(track)
    }
}

class PlayHistory(context: Context) : TrackHistory(context, "track-history.txt") {
    override fun addTrack(track: ApexTrack) {
        // Don't add the same track again if it's already the last one
        if (trackHistory.lastOrNull() != track)
            super.addTrack(track)
    }
}

sealed class TrackHistory(context: Context, val fileName: String) {
    companion object {
        private val LOGGER = Logger.getLogger(TrackHistory::class.java.name)
    }

    protected val historyFile = File(context.cacheDir, fileName)
        .also { if (!it.exists()) it.writeText("[]") }
    protected val trackHistory = historyFile.readLines()
        .mapNotNull { ApexTrack.TRACKS_DB[it] }
        .toMutableStateList()

    open fun addTrack(track: ApexTrack) {
        trackHistory.add(track)
        writeFile()
        LOGGER.info("Added ${track.name} (id=${track.id} to $fileName")
    }

    open fun removeTrack(track: ApexTrack) {
        trackHistory.remove(track)
        writeFile()
        LOGGER.info("Removed ${track.name} (id=${track.id} from $fileName")
    }

    open fun getTracks() = trackHistory.reversed()

    protected fun writeFile() {
        historyFile.writeText(trackHistory.joinToString("\n") { it.id })
    }
}
