package dev.reassembly.scrapguard.routers

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.reassembly.scrapguard.Scrapguard
import dev.reassembly.scrapguard.data.PlayerData
import dev.reassembly.scrapguard.utils.PlayerUtils
import org.bukkit.Material
import kotlin.math.abs
import kotlin.math.sqrt

class PacketRouter {

    fun routePacket(event: PacketReceiveEvent) {
        // Scrapguard.instance.launch {
            val data = Scrapguard.instance.playerDataRegistry.getPlayerData(event.user)!!

            handlePacketlessData(data)

            val checks = Scrapguard.instance.checkHandler.checks
            when (event.packetType) {
                PacketType.Play.Client.PLAYER_ROTATION -> {
                    val packet = WrapperPlayClientPlayerRotation(event)
                    Scrapguard.instance.launch {
                        handleRotationPacket(packet, data)
                        checks.forEach { check -> check.onPositionAndOrRotationPacket(event, data) }
                    }
                }

                PacketType.Play.Client.PLAYER_POSITION -> {
                    val packet = WrapperPlayClientPlayerPosition(event)
                    Scrapguard.instance.launch {
                        handlePositionPacket(packet, data)
                        checks.forEach { check -> check.onPositionAndOrRotationPacket(event, data) }
                    }
                }

                PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION -> {
                    val packet = WrapperPlayClientPlayerPositionAndRotation(event)
                    val posPacket = WrapperPlayClientPlayerPosition(
                        packet.position,
                        packet.isOnGround
                    )
                    val rotationPacket = WrapperPlayClientPlayerRotation(
                        packet.yaw,
                        packet.pitch,
                        packet.isOnGround
                    )

                    Scrapguard.instance.launch {
                        handlePositionPacket(posPacket, data)
                        handleRotationPacket(rotationPacket, data)
                        checks.forEach { check -> check.onPositionAndOrRotationPacket(event, data) }
                    }
                }

                PacketType.Play.Client.PLAYER_ABILITIES -> {
                    val wrapper = WrapperPlayClientPlayerAbilities(event)
                    Scrapguard.instance.launch {
                        handleAbilitiesPacket(wrapper, data)
                        checks.forEach { check -> check.onAbilitiesPacket(wrapper, data) }
                    }
                }

                PacketType.Play.Client.INTERACT_ENTITY -> {
                    val wrapper = WrapperPlayClientInteractEntity(event)
                    Scrapguard.instance.launch {
                        checks.forEach { check -> check.onPlayerInteractEntityPacket(wrapper, data) }
                    }
                }
            }
        // }
    }

    private fun handlePacketlessData(data: PlayerData) {
        val player = data.getPlayer()
        data.actionsData.ticksSinceOffHandAnimation++
        data.actionsData.ticksSinceMainHandAnimation++
        data.loginTicks++

        if (player.isGliding) {
            data.actionsData.isFlyingWithElytra = true
            data.actionsData.ticksSinceElytra = 0
        } else {
            data.actionsData.isFlyingWithElytra = false
            data.actionsData.ticksSinceElytra++
        }
    }

    private suspend fun handlePositionPacket(packet: WrapperPlayClientPlayerPosition, data: PlayerData) {
        val player = data.getPlayer()

        val posX = packet.location.x
        val posY = packet.location.y
        val posZ = packet.location.z

        // At this time, the data's current pos is actually the last pos, the above defines current pos
        val veloX = abs(posX) - abs(data.kinematicsData.posX)
        val veloY = abs(posY) - abs(data.kinematicsData.posY)
        val veloZ = abs(posZ) - abs(data.kinematicsData.posZ)
        val veloXZ = sqrt(
            (veloX * veloX) + (veloZ * veloZ)
        )

        val accelX =  abs(veloX) - abs(data.kinematicsData.veloX)
        val accelY =  abs(veloY) - abs(data.kinematicsData.veloY)
        val accelZ =  abs(veloZ) - abs(data.kinematicsData.veloZ)
        val accelXZ = sqrt(
            (accelX * accelX) + (accelZ * accelZ)
        )

        val jerkX = abs(accelX) - abs(data.kinematicsData.accelX)
        val jerkY = abs(accelY) - abs(data.kinematicsData.accelY)
        val jerkZ = abs(accelZ) - abs(data.kinematicsData.accelZ)
        val jerkXZ = sqrt(
            (jerkX * jerkX) + (jerkZ * jerkZ)
        )

        data.kinematicsData.lastPosX = data.kinematicsData.posX
        data.kinematicsData.lastPosY = data.kinematicsData.posY
        data.kinematicsData.lastPosZ = data.kinematicsData.posZ

        data.kinematicsData.lastVeloX = data.kinematicsData.veloX
        data.kinematicsData.lastVeloY = data.kinematicsData.veloY
        data.kinematicsData.lastVeloZ = data.kinematicsData.veloZ
        data.kinematicsData.lastVeloXZ = data.kinematicsData.veloXZ

        data.kinematicsData.lastAccelX = data.kinematicsData.accelX
        data.kinematicsData.lastAccelY = data.kinematicsData.accelY
        data.kinematicsData.lastAccelZ = data.kinematicsData.accelZ
        data.kinematicsData.lastAccelXZ = data.kinematicsData.accelXZ

        data.kinematicsData.lastJerkX = data.kinematicsData.jerkX
        data.kinematicsData.lastJerkY = data.kinematicsData.jerkY
        data.kinematicsData.lastJerkZ = data.kinematicsData.jerkZ
        data.kinematicsData.lastJerkXZ = data.kinematicsData.jerkXZ

        data.kinematicsData.posX = posX
        data.kinematicsData.posY = posY
        data.kinematicsData.posZ = posZ

        data.kinematicsData.veloX = veloX
        data.kinematicsData.veloY = veloY
        data.kinematicsData.veloZ = veloZ
        data.kinematicsData.veloXZ = veloXZ

        data.kinematicsData.accelX = accelX
        data.kinematicsData.accelY = accelY
        data.kinematicsData.accelZ = accelZ
        data.kinematicsData.accelXZ = accelXZ

        data.kinematicsData.jerkX = jerkX
        data.kinematicsData.jerkY = jerkY
        data.kinematicsData.jerkZ = jerkZ
        data.kinematicsData.jerkXZ = jerkXZ

        data.kinematicsData.serverOnGround = PlayerUtils.isActuallyOnGround(player).await()

        if (!data.kinematicsData.serverOnGround) {
            data.kinematicsData.airTicks++
        } else {
            data.kinematicsData.airTicks = 0
        }

        // Handle slime ticks
        val blocksBelow = PlayerUtils.getBlocksBelowPlayer(player).await()

        // Should note that this won't actually check if the user bounced off the slime block, just that they stood on or near one
        data.kinematicsData.slimeTicks++
        data.kinematicsData.iceTicks++
        if (blocksBelow.any { it.type == Material.SLIME_BLOCK }) {
            data.kinematicsData.slimeTicks = 0
        } else if (blocksBelow.any {it.type == Material.ICE || it.type == Material.BLUE_ICE || it.type == Material.PACKED_ICE}) {
            data.kinematicsData.iceTicks = 0
        }

    }

    private suspend fun handleRotationPacket(packet: WrapperPlayClientPlayerRotation, data: PlayerData) {
        data.kinematicsData.lastYaw = data.kinematicsData.yaw
        data.kinematicsData.lastPitch = data.kinematicsData.pitch
        data.kinematicsData.yaw = packet.yaw
        data.kinematicsData.pitch = packet.pitch
        data.kinematicsData.serverOnGround = PlayerUtils.isActuallyOnGround(data.getPlayer()).await()
    }

    private suspend fun handleAnimationPacket(packet: WrapperPlayClientAnimation, data: PlayerData) {
        if (packet.hand == InteractionHand.MAIN_HAND) {

        }
    }

    private suspend fun handleAbilitiesPacket(packet: WrapperPlayClientPlayerAbilities, data: PlayerData) {
        data.actionsData.lastCanAbilityFly = data.actionsData.canAbilityFly
        data.actionsData.canAbilityFly = packet.isFlying
    }
}