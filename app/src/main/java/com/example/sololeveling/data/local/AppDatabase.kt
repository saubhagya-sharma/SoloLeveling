package com.example.sololeveling.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sololeveling.data.local.dao.AchievementDao
import com.example.sololeveling.data.local.dao.ExerciseDao
import com.example.sololeveling.data.local.dao.ExercisePrDao
import com.example.sololeveling.data.local.dao.BossDao
import com.example.sololeveling.data.local.dao.DailyQuestDao
import com.example.sololeveling.data.local.dao.InventoryDao
import com.example.sololeveling.data.local.dao.MuscleStatDao
import com.example.sololeveling.data.local.dao.PlayerDao
import com.example.sololeveling.data.local.dao.StatDao
import com.example.sololeveling.data.local.dao.WorkoutExerciseDao
import com.example.sololeveling.data.local.dao.WorkoutSessionDao
import com.example.sololeveling.data.local.dao.WorkoutSetDao
import com.example.sololeveling.data.local.dao.TrophyDao
import com.example.sololeveling.data.local.entity.AchievementEntity
import com.example.sololeveling.data.local.entity.BossEntity
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.ExercisePrEntity
import com.example.sololeveling.data.local.entity.DailyQuestEntity
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.InventoryEntity
import com.example.sololeveling.data.local.entity.PlayerEntity
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity
import com.example.sololeveling.data.local.entity.WorkoutSetEntity
import com.example.sololeveling.data.local.entity.TrophyEntity

@Database(
    entities = [
        PlayerEntity::class,
        AchievementEntity::class,
        StatEntity::class,
        MuscleStatEntity::class,
        ExerciseEntity::class,
        ExercisePrEntity::class,
        DailyQuestEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        InventoryEntity::class,
        BossEntity::class,
        TrophyEntity::class
    ],
    version = 11
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun achievementDao(): AchievementDao
    abstract fun statDao(): StatDao
    abstract fun muscleStatDao(): MuscleStatDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exercisePrDao(): ExercisePrDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun bossDao(): BossDao
    abstract fun trophyDao(): TrophyDao
}
