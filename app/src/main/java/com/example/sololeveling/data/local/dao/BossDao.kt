package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.BossEntity

@Dao
interface BossDao {

    @Insert
    suspend fun insert(boss: BossEntity): Long

    @Query("SELECT * FROM boss WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveBoss(): BossEntity?

    @Update
    suspend fun update(boss: BossEntity)

    @Query("DELETE FROM boss WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM boss WHERE expiryDate < :today")
    suspend fun deleteExpired(today: String)
}
