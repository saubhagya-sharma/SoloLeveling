package com.example.sololeveling.core

import android.app.Activity
import com.example.sololeveling.SystemMessageManager
import com.example.sololeveling.data.local.AppDatabase
import com.example.sololeveling.data.local.entity.AchievementEntity
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AchievementManager {

    private var hostActivityRef: WeakReference<Activity>? = null

    data class MilestoneAchievement(
        val key: String,
        val milestone: Int,
        val title: String,
        val progressValue: Int
    )

    fun attachHost(activity: Activity) {
        hostActivityRef = WeakReference(activity)
    }

    suspend fun checkAchievements(database: AppDatabase) {
        val player = database.playerDao().getPlayer() ?: return
        var mutablePlayer = player

        val workoutCount = database.workoutSessionDao().countCompletedWorkouts()
        val prCount = database.exercisePrDao().countPrs()
        val questCount = player.totalQuestCompletions
        val weeklyCount = player.totalWeeklyGoalsCompleted
        val maxPrWeight = database.exercisePrDao().getMaxPrWeight() ?: 0.0

        val milestones = listOf(
            MilestoneAchievement("WORKOUT_1", 1, "Awakening", workoutCount),
            MilestoneAchievement("WORKOUT_10", 10, "Consistency I", workoutCount),
            MilestoneAchievement("WORKOUT_50", 50, "Gym Veteran", workoutCount),
            MilestoneAchievement("WORKOUT_100", 100, "Elite Trainee", workoutCount),
            MilestoneAchievement("WORKOUT_250", 250, "Master of Iron", workoutCount),
            MilestoneAchievement("WORKOUT_500", 500, "Lion Sin of Pride - Escanor", workoutCount),

            MilestoneAchievement("PR_1", 1, "First Limit Break", prCount),
            MilestoneAchievement("PR_10", 10, "Limit Breaker", prCount),
            MilestoneAchievement("PR_50", 50, "Beast Mode", prCount),

            MilestoneAchievement("QUEST_1", 1, "First Duty", questCount),
            MilestoneAchievement("QUEST_50", 50, "Consistency Master", questCount),
            MilestoneAchievement("QUEST_200", 200, "Eternal Routine", questCount),

            MilestoneAchievement("WEEKLY_1", 1, "Weekly Conqueror", weeklyCount),
            MilestoneAchievement("WEEKLY_10", 10, "Warrior Routine", weeklyCount),
            MilestoneAchievement("WEEKLY_50", 50, "Absolute Discipline", weeklyCount),

            MilestoneAchievement("STRENGTH_50", 50, "Strength Awakening", maxPrWeight.toInt()),
            MilestoneAchievement("STRENGTH_80", 80, "Power Surge", maxPrWeight.toInt()),
            MilestoneAchievement("STRENGTH_100", 100, "Century Lift", maxPrWeight.toInt()),
            MilestoneAchievement("STRENGTH_150", 150, "Titan Strength", maxPrWeight.toInt())
        )

        milestones.forEach { milestone ->
            if (milestone.progressValue >= milestone.milestone) {
                val existing = database.achievementDao().getAchievement(milestone.key)
                if (existing == null) {
                    database.achievementDao().insert(
                        AchievementEntity(
                            achievementKey = milestone.key,
                            unlocked = true,
                            dateUnlocked = nowDate()
                        )
                    )

                    mutablePlayer = mutablePlayer.copy(cheatMeals = mutablePlayer.cheatMeals + 1)
                    showMessage(
                        "ACHIEVEMENT UNLOCKED\n${milestone.title}\nReward: +1 Cheat Meal"
                    )
                }
            }
        }

        val level = GameManager.player.overallLevel()
        val levelMilestone = (level / 5) * 5
        if (levelMilestone >= 5 && levelMilestone > mutablePlayer.lastLevelMilestone) {
            mutablePlayer = mutablePlayer.copy(
                cheatMeals = mutablePlayer.cheatMeals + 1,
                lastLevelMilestone = levelMilestone
            )
            showMessage(
                "ACHIEVEMENT UNLOCKED\nRising Hunter\nLevel $levelMilestone Reached\nReward: +1 Cheat Meal"
            )
        }

        val workoutMilestone = (workoutCount / 20) * 20
        if (workoutMilestone >= 20 && workoutMilestone > mutablePlayer.lastWorkoutMilestone) {
            mutablePlayer = mutablePlayer.copy(
                cheatMeals = mutablePlayer.cheatMeals + 1,
                lastWorkoutMilestone = workoutMilestone
            )
            showMessage(
                "ACHIEVEMENT UNLOCKED\nThe Grinder\n$workoutMilestone Workouts Completed\nReward: +1 Cheat Meal"
            )
        }

        val questMilestone = (questCount / 28) * 28
        if (questMilestone >= 28 && questMilestone > mutablePlayer.lastQuestMilestone) {
            mutablePlayer = mutablePlayer.copy(
                cheatMeals = mutablePlayer.cheatMeals + 1,
                lastQuestMilestone = questMilestone
            )
            showMessage(
                "ACHIEVEMENT UNLOCKED\nDaily Devotion\n$questMilestone Quests Completed\nReward: +1 Cheat Meal"
            )
        }

        if (mutablePlayer != player) {
            database.playerDao().updatePlayer(mutablePlayer)
        }
    }

    private fun showMessage(message: String) {
        hostActivityRef?.get()?.let { activity ->
            SystemMessageManager.show(activity, message)
        }
    }

    private fun nowDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
