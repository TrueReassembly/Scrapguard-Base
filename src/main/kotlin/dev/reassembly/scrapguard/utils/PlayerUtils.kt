package dev.reassembly.scrapguard.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

object PlayerUtils {

    /**
     * Checks if the server reports that the user is on ground via checking the blocks below them. This does not
     * factor what the client claims.
     *
     * @return true if the server reports the user is on ground, false otherwise
     */
    suspend fun isActuallyOnGround(player: Player): Deferred<Boolean> = coroutineScope {
         async {
             val blocks = getBlocksBelowPlayer(player).await()
             blocks.forEach {
                 if (it.type.isSolid) {
                     return@async true
                 }
             }
             return@async false
        }
    }

    /**
     * Gets the blocks below the player
     */
    suspend fun getBlocksBelowPlayer(player: Player): Deferred<MutableList<Block>> = coroutineScope {
        async {
            val boundingBox = player.boundingBox
            val lowestY = boundingBox.minY
            var yDecrement = 0.01
            yDecrement += lowestY % 1

            val locations = listOf(
                Location(player.world, boundingBox.centerX, lowestY - yDecrement, boundingBox.centerZ), //XZ
                Location(player.world, boundingBox.minX, lowestY - yDecrement, boundingBox.centerZ), // -XZ
                Location(player.world, boundingBox.maxX, lowestY - yDecrement, boundingBox.centerZ), // +XZ
                Location(player.world, boundingBox.centerX, lowestY - yDecrement, boundingBox.minZ), // X-Z
                Location(player.world, boundingBox.centerX, lowestY - yDecrement, boundingBox.maxZ), // X+Z
                Location(player.world, boundingBox.minX, lowestY - yDecrement, boundingBox.minZ), // -X-Z
                Location(player.world, boundingBox.minX, lowestY - yDecrement, boundingBox.maxZ), // -X+Z
                Location(player.world, boundingBox.maxX, lowestY - yDecrement, boundingBox.minZ), // +X-Z
                Location(player.world, boundingBox.maxX, lowestY - yDecrement, boundingBox.maxZ), // +X+Z
                Location(player.world, player.x, lowestY - yDecrement, player.z).toCenterLocation() // Dead Center
            )

            val blocks = mutableListOf<Block>()
            locations.forEach {
                blocks.add(player.world.getBlockAt(it))
            }
            return@async blocks
        }
    }

}