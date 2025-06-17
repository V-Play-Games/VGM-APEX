package net.vpg.apex.entities

// All possible contexts are:
// - Album Info
// - Uploader Info
// - Random Picks
// - Play History
// - Search History
// - Search Results

interface ApexTrackContext {
    val name: String
    val tracks: List<ApexTrack>

    companion object {
        val EMPTY: ApexTrackContext = object : ApexTrackContext {
            override val name = ""
            override val tracks = emptyList<ApexTrack>()
        }
    }
}