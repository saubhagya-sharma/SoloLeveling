package com.example.sololeveling.data.local

import com.example.sololeveling.data.local.entity.ExerciseEntity

object DatabaseSeeder {
    suspend fun seedIfNeeded(database: AppDatabase) {
        val exerciseDao = database.exerciseDao()

        if (exerciseDao.getAll().isEmpty()) {
            exerciseDao.insertAll(
                listOf(
                    // --- 🟥 PUSH EXERCISES ---
                    ExerciseEntity(
                        name = "Incline Bench Press",
                        isTimeBased = false,
                        baseWeight = 15.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Flat Bench Press",
                        isTimeBased = false,
                        baseWeight = 15.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Tricep Rope Pushdowns",
                        isTimeBased = false,
                        baseWeight = 15.0,
                        strengthMultiplier = 2.5,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Arms"
                    ),
                    ExerciseEntity(
                        name = "Machine Chest Flyes",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 2.5,
                        enduranceMultiplier = 3.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Machine Chest Press",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Cable Chest Flyes",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Decline Bench Press",
                        isTimeBased = false,
                        baseWeight = 15.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Weighted Dips",
                        isTimeBased = false,
                        baseWeight = 60.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Chest"
                    ),
                    ExerciseEntity(
                        name = "Shoulder Press",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Shoulders"
                    ),
                    ExerciseEntity(
                        name = "Dumbell Lateral Raises",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Shoulders"
                    ),
                    ExerciseEntity(
                        name = "Overhead Dumbell Press",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Shoulders"
                    ),
                    ExerciseEntity(
                        name = "Overhead Dumbbell Extension",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Arms"
                    ),

                    // --- 🟦 PULL EXERCISES ---
                    ExerciseEntity(
                        name = "Lat Pulldowns",
                        isTimeBased = false,
                        baseWeight = 15.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Seated Cable Rows",
                        isTimeBased = false,
                        baseWeight = 20.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Hammer Curls",
                        isTimeBased = false,
                        baseWeight = 4.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Arms"
                    ),
                    ExerciseEntity(
                        name = "Weighted Pull Ups",
                        isTimeBased = false,
                        baseWeight = 60.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Barbell Bent Over Rows",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "T-Bar Rows",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Face Pulls",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 2.5,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Shoulders"
                    ),
                    ExerciseEntity(
                        name = "Dumbbell Shrugs",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 2.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Shoulders"
                    ),
                    ExerciseEntity(
                        name = "Barbell Bicep Curls",
                        isTimeBased = false,
                        baseWeight = 4.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 4.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Arms"
                    ),
                    ExerciseEntity(
                        name = "Preacher Curls",
                        isTimeBased = false,
                        baseWeight = 4.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.5,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Arms"
                    ),
                    ExerciseEntity(
                        name = "Single Arm Rows",
                        isTimeBased = false,
                        baseWeight = 4.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Back"
                    ),
                    ExerciseEntity(
                        name = "Dead Lift",
                        isTimeBased = false,
                        baseWeight = 20.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.5,
                        staminaMultiplier = 4.0,
                        primaryMuscleName = "Back"
                    ),

                    // --- 🟩 LEGS EXERCISES ---
                    ExerciseEntity(
                        name = "Leg Press",
                        isTimeBased = false,
                        baseWeight = 50.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Forward Lunges",
                        isTimeBased = false,
                        baseWeight = 1.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 4.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Romanian Dead Lift",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Barbell Squats",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Leg Extensions",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 3.5,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Leg Curls",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 3.5,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Hip Adductors",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 2.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Hip Abductors",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 2.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Standing Calf Raises",
                        isTimeBased = false,
                        baseWeight = 1.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Seated Calf Raises",
                        isTimeBased = false,
                        baseWeight = 10.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Bulgarian Split Squats",
                        isTimeBased = false,
                        baseWeight = 1.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Legs"
                    ),
                    ExerciseEntity(
                        name = "Goblet Squats",
                        isTimeBased = false,
                        baseWeight = 4.0,
                        strengthMultiplier = 4.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Legs"
                    ),

                    // --- 🟨 CARDIO & CORE ---
                    ExerciseEntity(
                        name = "Incline Treadmill Walk",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Static Cycle",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Machine Crunches",
                        isTimeBased = false,
                        baseWeight = 5.0,
                        strengthMultiplier = 2.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 1.5,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Stairmaster",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 2.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Cross Trainer",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Planks",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Bicycle Crunches",
                        isTimeBased = false,
                        baseWeight = 0.0,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 5.0,
                        staminaMultiplier = 3.0,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Russian Twists",
                        isTimeBased = false,
                        baseWeight = 1.0,
                        strengthMultiplier = 2.5,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Leg Raises",
                        isTimeBased = false,
                        baseWeight = 0.0,
                        strengthMultiplier = 3.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Ab Wheel Rollouts",
                        isTimeBased = false,
                        baseWeight = 0.0,
                        strengthMultiplier = 5.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Core"
                    ),
                    ExerciseEntity(
                        name = "Jump Rope",
                        isTimeBased = true,
                        baseWeight = null,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 3.0,
                        staminaMultiplier = 5.0,
                        primaryMuscleName = "Cardio"
                    ),
                    ExerciseEntity(
                        name = "Heel touches",
                        isTimeBased = false,
                        baseWeight = 0.0,
                        strengthMultiplier = 1.0,
                        enduranceMultiplier = 4.0,
                        staminaMultiplier = 2.0,
                        primaryMuscleName = "Core"
                    )
                )
            )
        }
    }
}