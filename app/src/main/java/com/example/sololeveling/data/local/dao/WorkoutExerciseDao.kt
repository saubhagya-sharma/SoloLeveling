package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity

@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExercise: WorkoutExerciseEntity): Long

    @Query("SELECT exercise.name FROM workout_exercise INNER JOIN exercise ON workout_exercise.exerciseId = exercise.id WHERE workout_exercise.id = :workoutExerciseId LIMIT 1")
    suspend fun getExerciseName(workoutExerciseId: Int): String?

    @Query("SELECT exercise.* FROM workout_exercise INNER JOIN exercise ON workout_exercise.exerciseId = exercise.id WHERE workout_exercise.id = :workoutExerciseId LIMIT 1")
    suspend fun getExerciseByWorkoutExerciseId(workoutExerciseId: Int): ExerciseEntity?

    @Query("DELETE FROM workout_exercise")
    suspend fun deleteAll()
}
