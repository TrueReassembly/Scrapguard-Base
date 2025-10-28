package dev.reassembly.scrapguard.listeners

import dev.reassembly.scrapguard.Scrapguard
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val data = Scrapguard.instance.playerDataRegistry.loadPlayer(event.player)
        data.clientBrand = event.player.clientBrandName ?: "Unknown"
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        Scrapguard.instance.playerDataRegistry.unloadPlayer(event.player)
    }
}