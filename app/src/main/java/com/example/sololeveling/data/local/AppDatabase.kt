package com.example.sololeveling.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.PlayerEntity
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity
import com.example.sololeveling.data.local.entity.WorkoutSetEntity

@Database(
    entities = [
        PlayerEntity::class,
        StatEntity::class,
        MuscleStatEntity::class,
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase()
