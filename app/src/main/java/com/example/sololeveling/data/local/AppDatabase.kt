package com.example.sololeveling.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sololeveling.data.local.dao.ExerciseDao
import com.example.sololeveling.data.local.dao.MuscleStatDao
import com.example.sololeveling.data.local.dao.PlayerDao
import com.example.sololeveling.data.local.dao.StatDao
import com.example.sololeveling.data.local.dao.WorkoutExerciseDao
import com.example.sololeveling.data.local.dao.WorkoutSessionDao
import com.example.sololeveling.data.local.dao.WorkoutSetDao
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
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun statDao(): StatDao
    abstract fun muscleStatDao(): MuscleStatDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSetDao(): WorkoutSetDao
}
