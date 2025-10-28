package dev.reassembly.scrapguard.listeners.packet

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import dev.reassembly.scrapguard.Scrapguard
import dev.reassembly.scrapguard.data.PlayerData
import dev.reassembly.scrapguard.data.SubData
import dev.reassembly.scrapguard.routers.PacketRouter
import dev.reassembly.scrapguard.utils.TextUtils.toMiniMessage
import java.lang.reflect.Modifier

object GenericPacketListener : PacketListener {

    override fun onPacketReceive(event: PacketReceiveEvent) {
        // Handle packet sniffing
        try {
            val playerData = Scrapguard.instance.playerDataRegistry.getPlayerData(event.user) ?: return

            if (playerData.packetsFilter.contains(event.packetType.name)) {
                val wrapperClass = event.packetType.wrapperClass ?: return
                val instance = wrapperClass.getConstructor(PacketReceiveEvent::class.java).newInstance(event)

                val packetName = instance.packetTypeData.packetType!!.name
                val packetData: MutableMap<String, Any> = getWrapperData(wrapperClass, instance).toMutableMap()
                packetData.putAll(getExtraData(playerData, packetName))
                var hoverText = ""
                for (dataPair in packetData) {
                    hoverText += "<grey>${dataPair.key}</grey> - <dark_aqua>${dataPair.value}<newline>"
                }
                hoverText = hoverText.removeSuffix("<newline>")

                val msg =
                    "<hover:show_text:'$hoverText'><grey>[<dark_aqua>?</dark_aqua>]<grey> $packetName</hover>".toMiniMessage()
                event.user.sendMessage(msg)

            }
        } catch (_: NoSuchElementException) {
            return
        }

        Scrapguard.instance.packetRouter.routePacket(event)
    }

    private fun getWrapperData(clazz: Class<*>, wrapper: Any, data: Map<String, Any> = emptyMap()): Map<String, Any> {
        if (!PacketWrapper::class.java.isAssignableFrom(clazz.superclass)) return data

        val data = clazz.declaredFields.onEach { it.isAccessible = true }
            .filter { !Modifier.isStatic(it.modifiers) && !it.isSynthetic }
            .associate { it.name to it.get(wrapper) }
            .toMutableMap()

        data.putAll(getWrapperData(clazz.superclass, wrapper, data))

        return data
    }

    private fun getExtraData(data: PlayerData, packetName: String): Map<String, Any> {
        val addedData = mutableMapOf<String, Any>()

        chosenDataIter@
        for (extraData in data.includeDataWithPacket.entries) {

            if (extraData.key == packetName) {

                packetDataIter@
                for (dataString in extraData.value) {

                    if (dataString.contains('.')) {
                        val splitDataString = dataString.split(".")
                        val dataClass = data.javaClass.declaredFields.onEach { it.isAccessible = true }
                            .first {
                                val annotation = it.getAnnotation(SubData::class.java)?.identifier ?: false
                                annotation == splitDataString[0]
                            }
                            .get(data)

                        val entry = dataClass.javaClass.declaredFields.onEach { it.isAccessible = true }
                            .filter { it.name == splitDataString[1] }
                            .associate { it.name to it.get(dataClass) }
                            .toMap()

                        addedData.putAll(entry)
                    } else {
                        val entry = data.javaClass.declaredFields.onEach { it.isAccessible = true }
                            .filter { it.name == dataString }
                            .associate { it.name to it.get(data) }
                            .toMap()
                        addedData.putAll(entry)
                    }

                }

            }
        }

        return addedData
    }
}