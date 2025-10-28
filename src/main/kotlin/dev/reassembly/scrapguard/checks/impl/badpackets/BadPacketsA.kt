package dev.reassembly.scrapguard.checks.impl.badpackets

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities
import dev.reassembly.scrapguard.checks.Check
import dev.reassembly.scrapguard.checks.CheckInfo
import dev.reassembly.scrapguard.checks.CheckType
import dev.reassembly.scrapguard.data.PlayerData

@CheckInfo(type = CheckType.BAD_PACKETS, "A", "Checks for nonsensical abilities packets", violationsToKick = 1)
class BadPacketsA : Check() {
    override fun onAbilitiesPacket(packet: WrapperPlayClientPlayerAbilities, data: PlayerData) {
        if (!packet.isFlying && !data.actionsData.lastCanAbilityFly) fail(
            data,
            "packetFly" to packet.isFlying,
            "lastPacketFly" to data.actionsData.lastCanAbilityFly
            )
    }
}