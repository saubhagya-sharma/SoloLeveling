package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.StatEntity

@Dao
interface StatDao {
    @Insert
    suspend fun insertAll(stats: List<StatEntity>)

    @Query("SELECT * FROM stat")
    suspend fun getAll(): List<StatEntity>

    @Update
    suspend fun update(stat: StatEntity)

    @Query("DELETE FROM stat")
    suspend fun deleteAll()
}
