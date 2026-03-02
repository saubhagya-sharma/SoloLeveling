package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity

@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExercise: WorkoutExerciseEntity): Long

    @Query("DELETE FROM workout_exercise")
    suspend fun deleteAll()
}
