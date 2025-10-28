package dev.reassembly.scrapguard.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import dev.reassembly.scrapguard.Scrapguard
import org.bukkit.entity.Player

@CommandAlias("sniff")
@CommandPermission("scrapguard.admin")
object SniffCommand: BaseCommand() {

    @Subcommand("set")
    @CommandCompletion("true|false @packets")
    fun onSniff(player: Player, whitelist: Boolean, packets: Array<String>) {
        val data = Scrapguard.instance.playerDataRegistry.getPlayerData(player)
        val rawPacketNames = mutableListOf<String>()
        val extraDataRegex = Regex(pattern = "\\(.*\\)", options = setOf(RegexOption.IGNORE_CASE))

        if (whitelist) {
            packets.forEach {
                val parsedBrackets = extraDataRegex.find(it) ?: return@forEach
                val packetName = it.removeSuffix(parsedBrackets.value)
                val packetData = it.removePrefix("$packetName(").removeSuffix(")")
                data.includeDataWithPacket[packetName] = mutableListOf()
                for (value in packetData.split("|")) {
                    data.includeDataWithPacket[packetName]?.add(value) ?: continue
                }
                rawPacketNames.add(packetName)
                println(packetName)
            }

            data.packetsFilter = PacketType.Play.Client.entries.filter { rawPacketNames.contains(it.name) }.map { it.name }
        } else {
            packets.forEach {
                rawPacketNames.add(it)
            }

            data.packetsFilter = PacketType.Play.Client.entries.filter { !rawPacketNames.contains(it.name) }.map { it.name }
        }
    }

    @CommandAlias("clear")
    fun onSniffClear(player: Player) {
        val data = Scrapguard.instance.playerDataRegistry.getPlayerData(player)
        data.packetsFilter = listOf()
    }
}