package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.ExercisePrEntity

@Dao
interface ExercisePrDao {
    @Query("SELECT * FROM exercise_pr WHERE exerciseId = :exerciseId")
    suspend fun getPr(exerciseId: Int): ExercisePrEntity?

    @Insert
    suspend fun insert(pr: ExercisePrEntity)

    @Update
    suspend fun update(pr: ExercisePrEntity)

    @Query("SELECT COUNT(*) FROM exercise_pr")
    suspend fun countPrs(): Int

    @Query("SELECT MAX(prWeight) FROM exercise_pr")
    suspend fun getMaxPrWeight(): Double?
}
