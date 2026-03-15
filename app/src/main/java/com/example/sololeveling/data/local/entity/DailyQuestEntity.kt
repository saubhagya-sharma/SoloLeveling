package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_quest")
data class DailyQuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val questType: String,
    val completed: Boolean
)
