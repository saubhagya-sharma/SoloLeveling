package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_session WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): WorkoutSessionEntity?

    @Insert
    suspend fun insert(session: WorkoutSessionEntity): Long

    @Query("DELETE FROM workout_session")
    suspend fun deleteAll()
}
