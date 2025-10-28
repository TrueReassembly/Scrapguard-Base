package dev.reassembly.scrapguard.utils

import kotlin.math.sqrt

object MathUtils {

    /**
     * Performs the pythagorean theorum on two numbers (a^2 + b^2 = c^2). This will return c
     */
    fun pythag(a: Double, b: Double): Double {
        return sqrt(a*a + b*b)
    }
}