package dev.reassembly.scrapguard.exempt

import dev.reassembly.scrapguard.data.PlayerData

enum class ExemptType(val exemption: (PlayerData) -> Boolean) {

    LOGIN({it.loginTicks < 100}),
    SLIME({it.kinematicsData.slimeTicks < 30}),
    ELYTRA({(it.actionsData.isFlyingWithElytra) || (!it.actionsData.isFlyingWithElytra && it.actionsData.ticksSinceElytra < 50) }),
    FLYING({it.getPlayer().isFlying}),
    STILL({
        it.kinematicsData.posX == it.kinematicsData.lastPosX &&
        it.kinematicsData.posY == it.kinematicsData.lastPosY &&
        it.kinematicsData.posZ == it.kinematicsData.lastPosZ
    }),
    ICE({it.kinematicsData.iceTicks < 50})
}