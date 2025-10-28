package dev.reassembly.scrapguard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object TextUtils {

    fun String.toMiniMessage(): Component {
        return MiniMessage.miniMessage().deserialize(this)
    }
}