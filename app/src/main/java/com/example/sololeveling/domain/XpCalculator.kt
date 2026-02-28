package com.example.sololeveling.domain

object XpCalculator {
    fun calculateRepXp(
        reps: Int,
        weight: Double,
        baseWeight: Double,
        multiplier: Double
    ): Double {
        if (reps <= 0) return 0.0

        val intensityFactor = if (baseWeight <= 0.0) {
            1.0
        } else {
            1.0 + (weight / baseWeight)
        }

        return reps * multiplier * intensityFactor
    }

    fun calculateTimeXp(
        minutes: Double,
        multiplier: Double
    ): Double {
        if (minutes <= 0.0) return 0.0
        return minutes * multiplier
    }
}
