package dev.reassembly.scrapguard.data

import dev.reassembly.scrapguard.data.subdata.ActionsData
import dev.reassembly.scrapguard.data.subdata.KinematicsData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

data class PlayerData(val uuid: UUID) {

    @SubData("kinematics") val kinematicsData = KinematicsData()
    @SubData("actions") val actionsData = ActionsData()
    var playerName: String = Bukkit.getPlayer(uuid)!!.name
    var loginTicks: Long = 0
    var clientBrand: String = "Unknown"
    var ping: Int = 0

    // Violation Logs
    val buffers = hashMapOf<String, Int>()
    val violations = hashMapOf<String, Int>()

    // Debugging
    var packetsFilter = listOf<String>()
    var includeDataWithPacket = hashMapOf<String, MutableList<String>>()
    // The below code is completely useless but i accidentally created the worst map instead of the above code at 2am and i find this very funny
    // var includeDataWithPackets = listOf<Pair<String, List<String>>>() // A list of pairs, first entry is the packet and the second is a list of the data to include

    // Methods
    fun getPlayer(): Player {
        return Bukkit.getPlayer(uuid)!!
    }
}
