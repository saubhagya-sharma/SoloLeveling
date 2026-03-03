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

    @Query("SELECT * FROM stat WHERE type = :type LIMIT 1")
    suspend fun getByType(type: String): StatEntity?

    @Query("UPDATE stat SET level = :level, currentXp = :currentXp WHERE type = :type")
    suspend fun updateStat(type: String, level: Int, currentXp: Double)

    @Update
    suspend fun update(stat: StatEntity)

    @Query("DELETE FROM stat")
    suspend fun deleteAll()
}
