package net.vpg.apex.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.vpg.apex.entities.ApexTrack

object RandomPicks {
    var currentPicks by mutableStateOf(ApexTrackContextDynamic("Random Picks", emptyList()))
        private set

    init {
        refresh()
    }

    fun refresh() {
        currentPicks = ApexTrackContextDynamic("Random Picks", ApexTrack.TRACKS_DB.values.shuffled().take(5))
    }
}