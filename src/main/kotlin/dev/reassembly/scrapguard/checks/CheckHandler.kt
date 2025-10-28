package dev.reassembly.scrapguard.checks

import org.reflections.Reflections

class CheckHandler {
    var checks = listOf<Check>()
        private set

    init {
        loadAllChecksByPackage("dev.reassembly.scrapguard.checks.impl")
    }

    fun loadAllChecksByPackage(pckgName: String) {

        val checksMutable = mutableListOf<Check>()

        val reflection = Reflections(pckgName)

            reflection.getSubTypesOf(Check::class.java)
            .forEach { checkClass ->
                val check = checkClass.getConstructor().newInstance()
                checksMutable.add(check)
                println("Registered check ${check.type} ${check.discriminator}")
            }

        checks = checksMutable
    }
}