package dev.reassembly.scrapguard.checks.impl.flight

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import dev.reassembly.scrapguard.checks.Check
import dev.reassembly.scrapguard.checks.CheckInfo
import dev.reassembly.scrapguard.checks.CheckType
import dev.reassembly.scrapguard.data.PlayerData

@CheckInfo(type = CheckType.FLIGHT, discriminator = "A", "Checks for spoofed onGround", buffer = 5, experimental = true)
class FlightA : Check() {
    override fun onPositionAndOrRotationPacket(packetEvent: PacketReceiveEvent, data: PlayerData) {
        val packet = convertToPositionRotationPacket(packetEvent)
        if (packet.isOnGround && !data.kinematicsData.serverOnGround) fail(
            data,
            "Client" to packet.isOnGround,
            "Server" to data.kinematicsData.serverOnGround)
    }
}