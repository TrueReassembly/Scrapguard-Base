package dev.reassembly.scrapguard

import co.aikar.commands.PaperCommandManager
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import dev.reassembly.scrapguard.checks.CheckHandler
import dev.reassembly.scrapguard.commands.SniffCommand
import dev.reassembly.scrapguard.listeners.PlayerConnectionListener
import dev.reassembly.scrapguard.listeners.packet.GenericPacketListener
import dev.reassembly.scrapguard.registries.PlayerDataRegistry
import dev.reassembly.scrapguard.routers.PacketRouter
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import java.util.function.Consumer

class Scrapguard : SuspendingJavaPlugin() {

    var playerDataRegistry = PlayerDataRegistry()
        private set
    lateinit var checkHandler: CheckHandler
        private set
    var packetRouter = PacketRouter()
        private set

    companion object {
        lateinit var instance: Scrapguard
            private set
    }

    override suspend fun onLoadAsync() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override suspend fun onEnableAsync() {
        instance = this
        registerPlayerData()
        registerPacketEvents()
        registerBukkitEvents()
        registerChecks()
        registerCommandManager()
    }

    override suspend fun onDisableAsync() {
        PacketEvents.getAPI().terminate()
    }

    private fun registerPacketEvents() {
        PacketEvents.getAPI().init()
        PacketEvents.getAPI().eventManager.registerListener(GenericPacketListener, PacketListenerPriority.NORMAL)
    }

    private fun registerBukkitEvents() {
        server.pluginManager.registerEvents(PlayerConnectionListener, this)
    }

    private fun registerCommandManager() {
        val commandManager = PaperCommandManager(this)
        commandManager.commandCompletions.registerCompletion("packets") {
            PacketType.Play.Client.entries.filter { it.wrapperClass != null }.map { it.name }
        }

        commandManager.registerCommand(SniffCommand)
    }

    private fun registerPlayerData() {
        playerDataRegistry = PlayerDataRegistry()

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Consumer {
            playerDataRegistry.resetBuffers()
        }, 20 * 2, 20 * 2)
    }

    private fun registerChecks() {
        checkHandler = CheckHandler()
    }
}
