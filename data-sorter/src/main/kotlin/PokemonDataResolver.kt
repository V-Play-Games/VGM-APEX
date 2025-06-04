import net.vpg.vjson.parser.JSONParser.toJSON
import java.io.File

fun main() {
    val rawTrackDataList = File("tracks.json")
        .toJSON()
        .toArray()
        .map { RawTrackData(it.toObject()) }
}