package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.TrophyEntity

@Dao
interface TrophyDao {
    @Insert
    suspend fun insert(trophy: TrophyEntity)

    @Query("SELECT * FROM trophy")
    suspend fun getAll(): List<TrophyEntity>
}
