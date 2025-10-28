package dev.reassembly.scrapguard.checks

enum class CheckType(val displayName: String) {
    NONE("Check Not Initialized Correctly"),
    BAD_PACKETS("Bad Packets"),
    FLIGHT("Flight"),
    SPEED("Speed"),
    KILLAURA("KillAura"),
    REACH("Reach"),
    NO_FALL("No Fall"),
    MOTION("Motion"),
    CRITICALS("Criticals")

}