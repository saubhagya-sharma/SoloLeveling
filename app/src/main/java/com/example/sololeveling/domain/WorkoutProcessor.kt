package com.example.sololeveling.domain

import com.example.sololeveling.core.GameManager

class WorkoutProcessor {
    private companion object {
        private const val MUSCLE_XP_SCALE = 0.5
    }
    fun processRepWorkout(
        exercise: Exercise,
        reps: Int,
        weight: Double,
        stats: List<Stat>
    ) {
        if (exercise.isTimeBased) return

        for (stat in stats) {
            val multiplier = when (stat.type) {
                StatType.STRENGTH -> exercise.strengthMultiplier
                StatType.ENDURANCE -> exercise.enduranceMultiplier
                StatType.STAMINA -> exercise.staminaMultiplier
                StatType.DISCIPLINE -> null
            }

            if (multiplier != null) {
                val xp = XpCalculator.calculateRepXp(
                    reps = reps,
                    weight = weight,
                    baseWeight = exercise.baseWeight ?: 0.0,
                    multiplier = multiplier
                )
                stat.addXp(xp)

                if (stat.type == StatType.STRENGTH) {
                    val muscleXp = xp * MUSCLE_XP_SCALE
                    val muscle = GameManager.player.getMuscle(exercise.primaryMuscleName)
                    muscle?.addXp(muscleXp)
                }
            }
        }
    }

    fun processTimeWorkout(
        exercise: Exercise,
        minutes: Double,
        stats: List<Stat>
    ) {
        if (!exercise.isTimeBased) return

        for (stat in stats) {
            val multiplier = when (stat.type) {
                StatType.STRENGTH -> exercise.strengthMultiplier
                StatType.ENDURANCE -> exercise.enduranceMultiplier
                StatType.STAMINA -> exercise.staminaMultiplier
                StatType.DISCIPLINE -> null
            }

            if (multiplier != null) {
                val xp = XpCalculator.calculateTimeXp(
                    minutes = minutes,
                    multiplier = multiplier
                )
                stat.addXp(xp)
            }
        }
    }
}
