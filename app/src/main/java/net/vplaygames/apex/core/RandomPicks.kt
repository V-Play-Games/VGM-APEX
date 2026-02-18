package net.vplaygames.apex.core

import net.vplaygames.apex.entities.ApexTrack
import net.vplaygames.apex.entities.ApexTrackContext

object RandomPicks : ApexTrackContext {
    override val name = "Random Picks"
    override var tracks: List<ApexTrack> = emptyList()
        private set

    init {
        refresh()
    }

    fun refresh() {
        tracks = ApexTrack.TRACKS_DB.values.shuffled().take(5)
    }
}
