package com.example.sololeveling.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE workout_set ADD COLUMN setNumber INTEGER NOT NULL DEFAULT 1")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE player ADD COLUMN muscleUnlocked INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE player ADD COLUMN weeklyGoalDays INTEGER NOT NULL DEFAULT 4")
            db.execSQL("ALTER TABLE player ADD COLUMN weeklyVisits INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN lastVisitDate TEXT")
            db.execSQL("ALTER TABLE player ADD COLUMN lastWeekResetDate TEXT")
        }
    }


    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS exercise_pr(
                    exerciseId INTEGER PRIMARY KEY NOT NULL,
                    prWeight REAL NOT NULL,
                    prReps INTEGER NOT NULL,
                    dateAchieved TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS daily_quest(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date TEXT NOT NULL,
                    questType TEXT NOT NULL,
                    completed INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }


    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE player ADD COLUMN cheatMeals INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN totalQuestCompletions INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN totalWeeklyGoalsCompleted INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN lastLevelMilestone INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN lastWorkoutMilestone INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE player ADD COLUMN lastQuestMilestone INTEGER NOT NULL DEFAULT 0")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS achievement(
                    achievementKey TEXT PRIMARY KEY NOT NULL,
                    unlocked INTEGER NOT NULL,
                    dateUnlocked TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }


    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS inventory(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    type TEXT NOT NULL,
                    createdDate TEXT NOT NULL,
                    expiryDate TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS boss(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    exerciseId INTEGER NOT NULL,
                    exerciseName TEXT NOT NULL,
                    bossName TEXT NOT NULL,
                    requiredWeight REAL,
                    requiredReps INTEGER,
                    requiredMinutes REAL,
                    attemptsLeft INTEGER NOT NULL DEFAULT 3,
                    createdDate TEXT NOT NULL,
                    expiryDate TEXT NOT NULL,
                    isCompleted INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS trophy(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    bossName TEXT NOT NULL,
                    exerciseName TEXT NOT NULL,
                    dateEarned TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL("ALTER TABLE workout_session ADD COLUMN isBossSession INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE workout_session ADD COLUMN bossName TEXT")
        }
    }


    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE player ADD COLUMN lastBossRewardWorkoutCount INTEGER NOT NULL DEFAULT 0")
        }
    }

    private val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE player ADD COLUMN lastBossRewardMilestone INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE player SET lastBossRewardMilestone = lastBossRewardWorkoutCount / 1")
        }
    }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "solo_leveling_db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
