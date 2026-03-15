package com.example.sololeveling.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sololeveling.data.local.entity.DailyQuestEntity

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quest WHERE date = :date")
    suspend fun getQuestsForDate(date: String): List<DailyQuestEntity>

    @Insert
    suspend fun insertAll(quests: List<DailyQuestEntity>)

    @Update
    suspend fun updateQuest(quest: DailyQuestEntity)

    @Query("DELETE FROM daily_quest WHERE date != :today")
    suspend fun clearOldQuests(today: String)
}
