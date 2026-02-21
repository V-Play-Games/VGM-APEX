package net.vplaygames.apex.entities

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
