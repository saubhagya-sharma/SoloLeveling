package com.example.sololeveling.domain

class PlayerProfile(
    val name: String,
    val stats: List<Stat>
) {
    fun overallLevel(): Int {
        if (stats.isEmpty()) return 0
        return stats.sumOf { it.level } / stats.size
    }

    fun isMuscleUnlocked(): Boolean = overallLevel() >= 5

    fun getStat(type: StatType): Stat? = stats.find { it.type == type }
}
