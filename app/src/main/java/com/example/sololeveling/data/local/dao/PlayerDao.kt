package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.PlayerEntity

@Dao
interface PlayerDao {
    @Insert
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM player LIMIT 1")
    suspend fun getPlayer(): PlayerEntity?

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("DELETE FROM player")
    suspend fun deleteAll()

    @Query("UPDATE player SET muscleUnlocked = :value WHERE id = 1")
    suspend fun updateMuscleUnlocked(value: Boolean)
}
