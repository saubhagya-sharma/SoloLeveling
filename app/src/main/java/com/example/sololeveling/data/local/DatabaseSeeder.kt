package com.example.sololeveling.data.local

import com.example.sololeveling.data.local.entity.ExerciseEntity

object DatabaseSeeder {
    suspend fun seedIfNeeded(database: AppDatabase) {
        val exerciseDao = database.exerciseDao()

        if (exerciseDao.getAll().isEmpty()) {
            exerciseDao.insertAll(
                listOf(
                    ExerciseEntity(
                        name = "Incline Bench Press",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Barbell Squats",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Weighted Pullups",
                        isTimeBased = false,
                        baseWeight = 60.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Pushups",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Running",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Cycling",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Treadmill",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Cardio"
                    )
                )
            )
        }
    }
}
