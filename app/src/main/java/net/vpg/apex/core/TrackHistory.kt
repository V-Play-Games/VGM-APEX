package net.vpg.apex.core

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexTrackContext
import java.io.File
import kotlin.math.min

class SearchHistory(context: Context) : SaveableTrackHistory(
    name = "Search History",
    context = context,
    fileName = "search-history.txt"
) {
    override fun addTrack(track: ApexTrack) {
        tracks.remove(track)
        super.addTrack(track)
    }
}

class PlayHistory(context: Context) : SaveableTrackHistory(
    name = "Playing History",
    context = context,
    fileName = "track-history.txt"
) {
    override fun addTrack(track: ApexTrack) {
        if (tracks.firstOrNull() != track)
            super.addTrack(track)
    }
}

sealed class SaveableTrackHistory(name: String, context: Context, val fileName: String) : TrackHistory(name) {
    companion object {
        private val tag = SaveableTrackHistory::class.java.name
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
        Log.i(tag, "Added ${track.title} (id=${track.id}) to $fileName")
    }

    override fun removeIndex(index: Int) {
        val track = tracks[index]
        super.removeIndex(index)
        writeFile()
        Log.i(tag, "Removed ${track.title} (id=${track.id}) from $fileName")
    }

    protected fun writeFile() {
        historyFile.writeText(
            tracks
                .filter { it != ApexTrack.EMPTY }
                .joinToString("\n") { it.id }
        )
    }
}

open class TrackHistory(override val name: String) : ApexTrackContext {
    companion object {
        private const val NOT_DISPLAYED = 1
        private const val DISPLAYED = 2
        private const val REMOVED = 3
    }

    private val appearingOnScreen = mutableStateListOf<Int>()
    override val tracks = mutableStateListOf<ApexTrack>()

    constructor(name: String, tracks: List<ApexTrack>) : this(name) {
        tracks.reversed().forEach { addTrack(it) }
    }

    open fun addTrack(track: ApexTrack) {
        tracks.add(0, track)
        appearingOnScreen.add(0, NOT_DISPLAYED)
    }

    open fun removeTrack(track: ApexTrack) {
        tracks.indexOf(track)
            .takeIf { it != -1 }
            ?.also { index ->
                removeIndex(index)
                removeTrack(track)
            }
    }

    open fun removeIndex(index: Int) {
        appearingOnScreen[index] = REMOVED
    }

    @Composable
    fun ComposeToList(
        limit: Int = tracks.size,
        emptyFallback: @Composable () -> Unit,
        lazyComposable: @Composable (LazyListScope.() -> Unit) -> Unit,
        content: @Composable (Int) -> Unit
    ) {
        if (tracks.none { it != ApexTrack.EMPTY }) {
            emptyFallback()
            return
        }
        lazyComposable {
            items(min(limit, tracks.size)) { index ->
                AnimatedVisibility(appearingOnScreen[index] == DISPLAYED) {
                    content(index)
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
        toRemove.forEach { tracks[it] = ApexTrack.EMPTY }
    }
}
