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

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "solo_leveling_db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
