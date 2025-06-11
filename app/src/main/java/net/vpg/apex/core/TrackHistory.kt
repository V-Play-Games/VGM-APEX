package net.vpg.apex.core

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import net.vpg.apex.entities.ApexTrack
import java.io.File
import java.util.logging.Logger
import kotlin.math.min

class SearchHistory(context: Context) : SaveableTrackHistory(context, "search-history.txt") {
    override fun addTrack(track: ApexTrack) {
        trackHistory.remove(track)
        super.addTrack(track)
    }
}

class PlayHistory(context: Context) : SaveableTrackHistory(context, "track-history.txt") {
    override fun addTrack(track: ApexTrack) {
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
        .also { file ->
            file.readLines()
                .mapNotNull { ApexTrack.TRACKS_DB[it] }
                .reversed()
                .forEach { super.addTrack(it) }
        }

    override fun addTrack(track: ApexTrack) {
        super.addTrack(track)
        writeFile()
        LOGGER.info("Added ${track.title} (id=${track.id}) to $fileName")
    }

    override fun removeIndex(index: Int) {
        val track = trackHistory[index]
        super.removeIndex(index)
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
        private const val NOT_DISPLAYED = 1
        private const val DISPLAYED = 2
        private const val REMOVED = 3
    }

    private val appearingOnScreen = mutableStateListOf<Int>()
    protected val trackHistory = mutableStateListOf<ApexTrack>()

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

    open fun removeIndex(index: Int) {
        appearingOnScreen[index] = REMOVED
    }

    fun isEmpty() = trackHistory.isEmpty()

    @Composable
    fun ComposeToList(
        limit: Int = trackHistory.size,
        emptyFallback: @Composable () -> Unit,
        lazyComposable: @Composable (LazyListScope.() -> Unit) -> Unit,
        content: @Composable (ApexTrack, Int) -> Unit
    ) {
        if (trackHistory.none { it != ApexTrack.EMPTY }) {
            emptyFallback()
            return
        }
        lazyComposable {
            items(min(limit, trackHistory.size)) { index ->
                AnimatedVisibility(appearingOnScreen[index] == DISPLAYED) {
                    content(trackHistory[index], index)
                }
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
        toRemove.forEach { trackHistory[it] = ApexTrack.EMPTY }
    }
}
