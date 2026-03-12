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

    @Query("SELECT * FROM workout_exercise WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: Int): List<WorkoutExerciseEntity>

    @Query(
        """
        SELECT 
            workout_exercise.id AS workoutExerciseId,
            exercise.name AS exerciseName,
            exercise.isTimeBased AS isTimeBased
        FROM workout_exercise
        INNER JOIN exercise
        ON workout_exercise.exerciseId = exercise.id
        WHERE workout_exercise.sessionId = :sessionId
        """
    )
    suspend fun getSessionExerciseDetails(sessionId: Int): List<SessionExerciseDetail>

    @Query("SELECT COUNT(*) FROM workout_exercise WHERE sessionId = :sessionId")
    suspend fun getExerciseCountForSession(sessionId: Int): Int

    @Query("DELETE FROM workout_exercise")
    suspend fun deleteAll()
}

data class SessionExerciseDetail(
    val workoutExerciseId: Int,
    val exerciseName: String,
    val isTimeBased: Boolean
)
