package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.WorkoutSetEntity

@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSetEntity)

    @Query("DELETE FROM workout_set")
    suspend fun deleteAll()
}
