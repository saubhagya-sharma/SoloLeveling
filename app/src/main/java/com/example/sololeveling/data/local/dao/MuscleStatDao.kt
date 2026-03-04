package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.MuscleStatEntity

@Dao
interface MuscleStatDao {
    @Insert
    suspend fun insertAll(muscles: List<MuscleStatEntity>)

    @Query("SELECT * FROM muscle_stat")
    suspend fun getAll(): List<MuscleStatEntity>

    @Query("SELECT * FROM muscle_stat WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): MuscleStatEntity?

    @Query("UPDATE muscle_stat SET level = :level, currentXp = :currentXp WHERE name = :name")
    suspend fun updateMuscle(name: String, level: Int, currentXp: Double)

    @Update
    suspend fun update(muscle: MuscleStatEntity)

    @Query("DELETE FROM muscle_stat")
    suspend fun deleteAll()
}
