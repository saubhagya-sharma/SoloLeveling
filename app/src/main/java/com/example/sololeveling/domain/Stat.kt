package com.example.sololeveling.domain

enum class StatType {
    STRENGTH,
    ENDURANCE,
    STAMINA,
    DISCIPLINE
}

data class Stat(
    val type: StatType,
    var level: Int = 1,
    var currentXp: Double = 0.0
) {
    private fun xpRequired(): Double = 150.0 * (level * level)

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
