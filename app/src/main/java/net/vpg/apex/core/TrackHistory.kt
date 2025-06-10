package net.vpg.apex.core

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import net.vpg.apex.entities.ApexTrack
import java.io.File
import java.util.logging.Logger

class SearchHistory(context: Context) : SaveableTrackHistory(context, "search-history.txt") {
    override fun addTrack(track: ApexTrack) {
        trackHistory.remove(track)
        super.addTrack(track)
    }
}

class PlayHistory(context: Context) : SaveableTrackHistory(context, "track-history.txt") {
    override fun addTrack(track: ApexTrack) {
        // Don't add the same track again if it's already the first one
        if (trackHistory.firstOrNull() != track)
            super.addTrack(track)
    }
}

sealed class SaveableTrackHistory(context: Context, val fileName: String) : TrackHistory() {
    companion object {
        private val LOGGER = Logger.getLogger(SaveableTrackHistory::class.java.name)
    }

    protected val historyFile = File(context.cacheDir, fileName)
        .also { if (!it.exists()) it.writeText("[]") }

    init {
        historyFile.readLines()
            .mapNotNull { ApexTrack.TRACKS_DB[it] }
            .reversed()
            .forEach { super.addTrack(it) }
    }

    override fun addTrack(track: ApexTrack) {
        super.addTrack(track)
        writeFile()
        LOGGER.info("Added ${track.title} (id=${track.id}) to $fileName")
    }

    override fun removeTrack(track: ApexTrack) {
        super.removeTrack(track)
        writeFile()
        LOGGER.info("Removed ${track.title} (id=${track.id}) from $fileName")
    }

    protected fun writeFile() {
        historyFile.writeText(
            trackHistory
                .filter { it != ApexTrack.EMPTY }
                .joinToString("\n") { it.id }
        )
    }
}

open class TrackHistory() {
    companion object {
        private val NOT_DISPLAYED = 1
        private val DISPLAYED = 2
        private val REMOVED = 3
    }

    private val appearingOnScreen = mutableStateListOf<Int>()
    protected val trackHistory = mutableListOf<ApexTrack>()

    constructor(tracks: List<ApexTrack>) : this() {
        tracks.reversed().forEach { addTrack(it) }
    }

    open fun addTrack(track: ApexTrack) {
        trackHistory.add(0, track)
        appearingOnScreen.add(0, NOT_DISPLAYED)
    }

    open fun removeTrack(track: ApexTrack) {
        trackHistory.indexOf(track)
            .takeIf { it != -1 }
            ?.also { index ->
                removeIndex(index)
                removeTrack(track)
            }
    }

    fun removeIndex(index: Int) {
        appearingOnScreen[index] = REMOVED
    }

    private fun forceRemoveIndex(index: Int) {
        trackHistory[index] = ApexTrack.EMPTY
    }

    fun isEmpty() = trackHistory.isEmpty()

    fun composeToList(
        scope: LazyListScope,
        limit: Int = trackHistory.size,
        content: @Composable (ApexTrack, Int) -> Unit
    ) {
        scope.items(limit) { index ->
            AnimatedVisibility(appearingOnScreen[index] == DISPLAYED) {
                content(trackHistory[index], index)
            }
        }
        val toRemove = mutableListOf<Int>()
        for (i in 0..appearingOnScreen.size - 1) {
            if (appearingOnScreen[i] == REMOVED) {
                toRemove.add(i)
            } else if (i >= limit) {
                appearingOnScreen[i] = NOT_DISPLAYED
            } else {
                appearingOnScreen[i] = DISPLAYED
            }
        }
        toRemove.forEach { forceRemoveIndex(it) }
    }
}
