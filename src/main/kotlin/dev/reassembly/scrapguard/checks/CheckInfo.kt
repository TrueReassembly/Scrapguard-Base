package dev.reassembly.scrapguard.checks

@Target(AnnotationTarget.CLASS)
annotation class CheckInfo(
    val type: CheckType,
    val discriminator: String,
    val description: String = "???",
    // The amount of times the check needs to fail before it's actually registed as a fail, buffers reset every 2 seconds.
    val buffer: Int = 0,
    val violationsToKick: Int = 0,
    val experimental: Boolean = false
)
