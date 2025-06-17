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

class SearchHistory(context: Context) : ApexTrackContextDynamic(
    name = "Recent Searches",
    saveFile = File(context.cacheDir, "search-history.txt")
) {
    override fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        tracks.remove(track)
        super.addTrack(track, trackContext)
    }
}

class PlayHistory(context: Context) : ApexTrackContextDynamic(
    name = "Recently Played",
    saveFile = File(context.cacheDir, "play-history.txt")
) {
    override fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        if (trackContext != this && tracks.firstOrNull() != track)
            super.addTrack(track, trackContext)
    }
}

open class ApexTrackContextDynamic(
    override val name: String,
    private val saveFile: File? = null
) : ApexTrackContext {
    companion object {
        private val tag = ApexTrackContextDynamic::class.java.name
        private const val NOT_DISPLAYED = 1
        private const val DISPLAYED = 2
        private const val REMOVED = 3
    }

    private val appearingOnScreen = mutableStateListOf<Int>()
    override val tracks = mutableStateListOf<ApexTrack>()

    init {
        saveFile
            ?.also { if (!it.exists()) it.writeText("[]") }
            ?.readLines()
            ?.mapNotNull { ApexTrack.TRACKS_DB[it] }
            ?.also { addAll(it, ApexTrackContext.EMPTY) }
    }

    constructor(name: String, tracks: List<ApexTrack>) : this(name) {
        addAll(tracks, ApexTrackContext.EMPTY)
    }

    fun addAll(tracks: List<ApexTrack>, trackContext: ApexTrackContext) {
        tracks.reversed().forEach { addTrack(it, trackContext) }
    }

    open fun addTrack(track: ApexTrack, trackContext: ApexTrackContext) {
        tracks.add(0, track)
        appearingOnScreen.add(0, NOT_DISPLAYED)
        writeFile("Added ${track.title} (id=${track.id}) to ${saveFile?.name}")
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
        val track = tracks[index]
        appearingOnScreen[index] = REMOVED
        writeFile("Removed ${track.title} (id=${track.id}) from ${saveFile?.name}")
    }

    private fun writeFile(log: String? = null) = saveFile?.also {
        saveFile.writeText(
            tracks
                .filter { it != ApexTrack.EMPTY }
                .joinToString("\n") { it.id }
        )
        log?.run { Log.i(tag, log) }
    }

    @Composable
    override fun ComposeToList(
        limit: Int,
        emptyFallback: @Composable () -> Unit,
        lazyComposable: @Composable (LazyListScope.() -> Unit) -> Unit,
        content: @Composable ApexTrackContext.(Int) -> Unit
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
        for (i in 0..<appearingOnScreen.size) {
            if (appearingOnScreen[i] == REMOVED) {
                toRemove.add(i)
            } else if (i >= limit) {
                appearingOnScreen[i] = NOT_DISPLAYED
            } else {
                appearingOnScreen[i] = DISPLAYED
            }
        }
        toRemove.forEach { tracks[it] = ApexTrack.EMPTY }
        writeFile()
    }
}
