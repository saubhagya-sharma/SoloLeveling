package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.ExerciseEntity

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insert(exercise: ExerciseEntity)

    @Insert
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("SELECT * FROM exercise")
    suspend fun getAll(): List<ExerciseEntity>

    @Query("SELECT * FROM exercise WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ExerciseEntity?

    @Query("DELETE FROM exercise")
    suspend fun deleteAll()
}
