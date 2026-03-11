package com.example.sololeveling.core

import com.example.sololeveling.domain.PlayerProfile
import com.example.sololeveling.domain.MuscleStat
import com.example.sololeveling.domain.Stat
import com.example.sololeveling.domain.StatType

object GameManager {
    lateinit var player: PlayerProfile
    var pendingGoalCompletion: Boolean = false
    var pendingExtraWorkout: Boolean = false
    fun initializePlayer(name: String) {
        val baseStats = listOf(
            Stat(type = StatType.STRENGTH),
            Stat(type = StatType.ENDURANCE),
            Stat(type = StatType.STAMINA),
            Stat(type = StatType.DISCIPLINE)
        )

        val baseMuscleList = listOf(
            MuscleStat(name = "Chest"),
            MuscleStat(name = "Back"),
            MuscleStat(name = "Legs"),
            MuscleStat(name = "Shoulders"),
            MuscleStat(name = "Arms"),
            MuscleStat(name = "Core")
        )

        player = PlayerProfile(name = name, stats = baseStats, muscleStats = baseMuscleList)
    }
}
