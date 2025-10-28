package dev.reassembly.scrapguard.data.subdata

class KinematicsData {
    // Position
    var posX = 0.0
    var posY = 0.0
    var posZ = 0.0
    var pitch = 0.0f
    var yaw = 0.0f
    var lastPosX = 0.0
    var lastPosY = 0.0
    var lastPosZ = 0.0
    var lastPitch = 0.0f
    var lastYaw = 0.0f

    // Velocity
    var veloX = 0.0
    var veloY = 0.0
    var veloZ = 0.0
    var veloXZ = 0.0
    var lastVeloX = 0.0
    var lastVeloY = 0.0
    var lastVeloZ = 0.0
    var lastVeloXZ = 0.0

    // Acceleration
    var accelX = 0.0
    var accelY = 0.0
    var accelZ = 0.0
    var accelXZ = 0.0
    var lastAccelX = 0.0
    var lastAccelY = 0.0
    var lastAccelZ = 0.0
    var lastAccelXZ= 0.0

    // Delta Acceleration
    var jerkX = 0.0
    var jerkY = 0.0
    var jerkZ = 0.0
    var jerkXZ = 0.0
    var lastJerkX = 0.0
    var lastJerkY = 0.0
    var lastJerkZ = 0.0
    var lastJerkXZ = 0.0

    var serverOnGround = true

    var airTicks = 0L
    var slimeTicks = 0L
    var iceTicks = 0L

    /**
     * Gets the high of the user's head (which is just posY + 1.62)
     *
     * @return the y position of the player's head
     */
    fun getHeadHeight(): Double {
        return posY + 1.62
    }


}
