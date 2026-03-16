package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sololeveling.data.local.entity.AchievementEntity

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement WHERE achievementKey = :key")
    suspend fun getAchievement(key: String): AchievementEntity?

    @Insert
    suspend fun insert(achievement: AchievementEntity)

    @Query("SELECT * FROM achievement")
    suspend fun getAll(): List<AchievementEntity>
}
