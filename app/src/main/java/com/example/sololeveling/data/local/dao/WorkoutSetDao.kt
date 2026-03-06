package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.WorkoutSetEntity

@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSetEntity): Long

    @Update
    suspend fun update(workoutSet: WorkoutSetEntity)

    @Query("SELECT * FROM workout_set WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getByWorkoutExerciseId(workoutExerciseId: Int): List<WorkoutSetEntity>

    @Query("SELECT * FROM workout_set WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber DESC LIMIT 1")
    suspend fun getMostRecentByWorkoutExerciseId(workoutExerciseId: Int): WorkoutSetEntity?

    @Query("SELECT MAX(setNumber) FROM workout_set WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun getMaxSetNumber(workoutExerciseId: Int): Int?

    @Query("DELETE FROM workout_set")
    suspend fun deleteAll()
}
