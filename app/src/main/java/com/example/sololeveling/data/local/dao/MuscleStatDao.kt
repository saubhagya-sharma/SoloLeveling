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

    @Update
    suspend fun update(muscle: MuscleStatEntity)

    @Query("DELETE FROM muscle_stat")
    suspend fun deleteAll()
}
