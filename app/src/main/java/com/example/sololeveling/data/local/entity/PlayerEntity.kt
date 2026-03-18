package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val muscleUnlocked: Boolean = false,
    val weeklyGoalDays: Int = 4,
    val weeklyVisits: Int = 0,
    val lastVisitDate: String? = null,
    val lastWeekResetDate: String? = null,
    val cheatMeals: Int = 0,
    val totalQuestCompletions: Int = 0,
    val totalWeeklyGoalsCompleted: Int = 0,
    val lastLevelMilestone: Int = 0,
    val lastWorkoutMilestone: Int = 0,
    val lastQuestMilestone: Int = 0,
    val lastBossRewardMilestone: Int = 0
)
