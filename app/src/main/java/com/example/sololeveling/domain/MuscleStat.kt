package com.example.sololeveling.domain

data class MuscleStat(
    val name: String,
    var level: Int = 1,
    var currentXp: Double = 0.0
) {
<<<<<<< codex/add-muscle-stats-domain-class
    fun xpRequired(): Double = 100.0 * level
=======
    fun xpRequired(): Double = 150.0 * level * level
>>>>>>> master

    fun addXp(amount: Double) {
        if (amount <= 0.0) return

        currentXp += amount

        while (currentXp >= xpRequired()) {
            currentXp -= xpRequired()
            level++
        }
    }

    fun progressToNextLevel(): Double = (currentXp / xpRequired()).coerceIn(0.0, 1.0)
}
