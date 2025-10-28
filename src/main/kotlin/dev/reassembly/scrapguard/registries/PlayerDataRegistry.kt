package dev.reassembly.scrapguard.registries

import com.github.retrooper.packetevents.protocol.player.User
import dev.reassembly.scrapguard.data.PlayerData
import org.bukkit.entity.Player
import java.util.*

class PlayerDataRegistry {

    private var onlineUserData = mutableListOf<PlayerData>()

    fun loadPlayer(player: Player): PlayerData {
        val data = PlayerData(player.uniqueId)
        onlineUserData.add(data)
        return data
    }

    fun getPlayerData(uuid: UUID): PlayerData {
        for (data in onlineUserData) {
            if (data.uuid == uuid) {
                return data
            }
        }
        throw NoSuchElementException("The UUID provided is not yet in the registry, have they been initialized yet?")
    }

    fun getPlayerData(player: Player): PlayerData {
        return getPlayerData(player.uniqueId)
    }

    fun getPlayerData(user: User): PlayerData? {
        return try {
            user.uuid
            getPlayerData(user.uuid)
        } catch (npe: NullPointerException) {
            null
        }
    }

    fun resetBuffers() {
        onlineUserData.forEach { it.buffers.clear() }
    }

    fun resetViolations() {
        onlineUserData.forEach { it.violations.clear() }
    }

    fun unloadPlayer(player: Player) {
        onlineUserData.remove(getPlayerData(player))
    }
}