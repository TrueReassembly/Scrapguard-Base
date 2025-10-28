plugins {
    kotlin("jvm") version "2.2.20-RC2"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.reassembly"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.codemc.io/repository/maven-releases/")

    maven("https://repo.codemc.io/repository/maven-snapshots/")

    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.22.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.22.0")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion(project.findProperty("mcServerVersion").toString())
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
    compilerOptions.javaParameters.set(true)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "dev.reassembly.scrapguard.acf")
    relocate("co.aikar.locales", "dev.reassembly.scrapguard.locales")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
