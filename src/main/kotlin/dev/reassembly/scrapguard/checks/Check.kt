package dev.reassembly.scrapguard.checks

import com.github.retrooper.packetevents.event.PacketEvent
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation
import dev.reassembly.scrapguard.Scrapguard
import dev.reassembly.scrapguard.data.PlayerData
import dev.reassembly.scrapguard.exempt.ExemptType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerKickEvent

abstract class Check {
    private var checkInfo = this.javaClass.getAnnotation(CheckInfo::class.java)
    val type = checkInfo.type
    val discriminator = checkInfo.discriminator
    val description = checkInfo.description
    val experimental = checkInfo.experimental
    val maxViolations = checkInfo.violationsToKick
    val bufferThreshold = checkInfo.buffer

    fun fail(data: PlayerData) {
        fail(data, "" to "")
    }

    fun fail(data: PlayerData, vararg debugData: Pair<String, Any>) {
        // See if we've exceeded the buffer in the last 2 seconds
        val buffer = data.buffers.getOrPut("$type$discriminator") { 0 } + 1
        data.buffers["$type$discriminator"] = buffer
        if (buffer < bufferThreshold) return

        var extraData = Component.text(description, NamedTextColor.DARK_AQUA).append(Component.newline())

        if (debugData[0].first != "") {

            for (theExtraData in debugData) {
                extraData = extraData
                    .append(Component.text(theExtraData.first, NamedTextColor.DARK_AQUA))
                    .append(Component.text(" = ", NamedTextColor.GRAY))
                    .append(Component.text(theExtraData.second.toString(), NamedTextColor.GRAY))
                    .append { if (theExtraData.first != debugData.last().first) Component.newline() else Component.empty() }
            }
        }

        var rawMsg = "<grey>[<dark_aqua>SCRAPGUARD</dark_aqua>]</grey> <dark_aqua>${data.playerName} <grey>failed</grey> ${type.displayName} $discriminator</dark_aqua>"

        if (maxViolations > 0) {
            val currentViolations = (data.violations.getOrPut("$type$discriminator") { 0 }) + 1
            data.violations["$type$discriminator"] = currentViolations
            rawMsg += " <grey>(<dark_aqua>$currentViolations</dark_aqua>/<dark_aqua>$maxViolations</dark_aqua>)"
            if (currentViolations >= maxViolations) {
                data.getPlayer().kick(Component.text("Kicked for Cheating"))
                data.violations.remove("$type$discriminator")
            }
        }
        if (experimental) rawMsg = rawMsg.replace("dark_aqua", "red")
        val msg = MiniMessage.miniMessage().deserialize(rawMsg).hoverEvent(HoverEvent.showText(extraData))
        Bukkit.broadcast(msg, "scrapguard.alerts")
    }

    /**
     * Converts a [PacketType.Play.Client.PLAYER_POSITION], [PacketType.Play.Client.PLAYER_ROTATION] or [PacketType.Play.Client.PLAYER_POSITION], [PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION] packet event into a [WrapperPlayClientPlayerPositionAndRotation] instance.
     * @param packetEvent A packetevent where packetType ==  [PacketType.Play.Client.PLAYER_POSITION], [PacketType.Play.Client.PLAYER_ROTATION] or [PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION]
     * @return An instance of [WrapperPlayClientPlayerPositionAndRotation]
     *
     * @throws IllegalArgumentException if the packet is not of a valid type listed above
     */
    fun convertToPositionRotationPacket(packetEvent: PacketReceiveEvent): WrapperPlayClientPlayerPositionAndRotation {

        if (packetEvent.packetType != PacketType.Play.Client.PLAYER_POSITION && packetEvent.packetType != PacketType.Play.Client.PLAYER_ROTATION && packetEvent.packetType != PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            throw IllegalArgumentException("Packet ${packetEvent.packetType.name} cannot be converted to a PositionAndRotation packet")
        }

        if (packetEvent.packetType == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            return WrapperPlayClientPlayerPositionAndRotation(packetEvent)
        }

        val data = Scrapguard.instance.playerDataRegistry.getPlayerData(packetEvent.user)!!
        var position = Vector3d(data.kinematicsData.posX, data.kinematicsData.posY, data.kinematicsData.posZ)
        var yaw = data.kinematicsData.yaw
        var pitch = data.kinematicsData.pitch
        var onGround: Boolean

        if (packetEvent.packetType == PacketType.Play.Client.PLAYER_POSITION) {
            val posPacket = WrapperPlayClientPlayerPosition(packetEvent)
            position = posPacket.position
            onGround = posPacket.isOnGround
        } else {
            val rotationPacket = WrapperPlayClientPlayerRotation(packetEvent)
            yaw = rotationPacket.yaw
            pitch = rotationPacket.pitch
            onGround = rotationPacket.isOnGround
        }
        return WrapperPlayClientPlayerPositionAndRotation(position, yaw, pitch, onGround)
    }

    fun isExempt(data: PlayerData, vararg exemptions: ExemptType): Boolean {
        for (exemption in exemptions) {
            if (exemption.exemption.invoke(data)) return true
        }
        return false
    }

    open fun onPositionAndOrRotationPacket(packetEvent: PacketReceiveEvent, data: PlayerData) {}
    open fun onAbilitiesPacket(packet: WrapperPlayClientPlayerAbilities, data: PlayerData) {}
    open fun onPlayerInteractEntityPacket(packet: WrapperPlayClientInteractEntity, data: PlayerData) {}
}