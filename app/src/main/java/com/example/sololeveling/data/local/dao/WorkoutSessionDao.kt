package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_session WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): WorkoutSessionEntity?

    @Query(
        """
        SELECT * FROM workout_session
        WHERE date = :date
        AND id IN (
            SELECT sessionId FROM workout_exercise
        )
        LIMIT 1
        """
    )
    suspend fun getSessionByDateWithExercises(date: String): WorkoutSessionEntity?

    @Query(
        """
        SELECT workout_session.date
        FROM workout_session
        INNER JOIN workout_exercise ON workout_exercise.sessionId = workout_session.id
        GROUP BY workout_session.id
        HAVING COUNT(workout_exercise.id) > 0
        """
    )
    suspend fun getWorkoutDatesWithExercises(): List<String>


    @Query(
        """
        SELECT COUNT(DISTINCT workout_session.id)
        FROM workout_session
        INNER JOIN workout_exercise ON workout_exercise.sessionId = workout_session.id
        """
    )
    suspend fun countCompletedWorkouts(): Int

    @Insert
    suspend fun insert(session: WorkoutSessionEntity): Long

    @Query("DELETE FROM workout_session")
    suspend fun deleteAll()
}
