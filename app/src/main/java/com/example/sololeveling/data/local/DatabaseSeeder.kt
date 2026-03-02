package com.example.sololeveling.data.local

import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.PlayerEntity
import com.example.sololeveling.data.local.entity.StatEntity

object DatabaseSeeder {
    suspend fun seedIfNeeded(database: AppDatabase) {
        if (database.playerDao().getPlayer() != null) return

        database.playerDao().insertPlayer(
            PlayerEntity(
                id = 1,
                name = ""
            )
        )

        database.statDao().insertAll(
            listOf(
                StatEntity(type = "STRENGTH", level = 1, currentXp = 0.0),
                StatEntity(type = "ENDURANCE", level = 1, currentXp = 0.0),
                StatEntity(type = "STAMINA", level = 1, currentXp = 0.0),
                StatEntity(type = "DISCIPLINE", level = 1, currentXp = 0.0)
            )
        )

        database.muscleStatDao().insertAll(
            listOf(
                MuscleStatEntity(name = "Chest", level = 1, currentXp = 0.0),
                MuscleStatEntity(name = "Back", level = 1, currentXp = 0.0),
                MuscleStatEntity(name = "Legs", level = 1, currentXp = 0.0),
                MuscleStatEntity(name = "Shoulders", level = 1, currentXp = 0.0),
                MuscleStatEntity(name = "Arms", level = 1, currentXp = 0.0),
                MuscleStatEntity(name = "Core", level = 1, currentXp = 0.0)
            )
        )

        database.exerciseDao().insertAll(
            listOf(
                ExerciseEntity(
                    name = "Bench Press",
                    isTimeBased = false,
                    baseWeight = 10.0,
                    strengthMultiplier = 3.5,
                    enduranceMultiplier = 1.0,
                    staminaMultiplier = 0.5,
                    primaryMuscleName = "Chest"
                ),
                ExerciseEntity(
                    name = "Squat",
                    isTimeBased = false,
                    baseWeight = 15.0,
                    strengthMultiplier = 3.5,
                    enduranceMultiplier = 1.2,
                    staminaMultiplier = 0.8,
                    primaryMuscleName = "Legs"
                ),
                ExerciseEntity(
                    name = "Deadlift",
                    isTimeBased = false,
                    baseWeight = 20.0,
                    strengthMultiplier = 4.0,
                    enduranceMultiplier = 0.8,
                    staminaMultiplier = 0.5,
                    primaryMuscleName = "Back"
                ),
                ExerciseEntity(
                    name = "Shoulder Press",
                    isTimeBased = false,
                    baseWeight = 8.0,
                    strengthMultiplier = 2.8,
                    enduranceMultiplier = 1.2,
                    staminaMultiplier = 0.6,
                    primaryMuscleName = "Shoulders"
                ),
                ExerciseEntity(
                    name = "Bicep Curl",
                    isTimeBased = false,
                    baseWeight = 5.0,
                    strengthMultiplier = 2.0,
                    enduranceMultiplier = 1.5,
                    staminaMultiplier = 0.5,
                    primaryMuscleName = "Arms"
                ),
                ExerciseEntity(
                    name = "Pushups",
                    isTimeBased = false,
                    baseWeight = 1.0,
                    strengthMultiplier = 1.8,
                    enduranceMultiplier = 2.0,
                    staminaMultiplier = 1.0,
                    primaryMuscleName = "Core"
                ),
                ExerciseEntity(
                    name = "Treadmill",
                    isTimeBased = true,
                    baseWeight = null,
                    strengthMultiplier = 0.0,
                    enduranceMultiplier = 2.5,
                    staminaMultiplier = 3.0,
                    primaryMuscleName = "Core"
                )
            )
        )
    }
}
