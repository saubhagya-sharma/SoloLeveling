package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement")
data class AchievementEntity(
    @PrimaryKey val achievementKey: String,
    val unlocked: Boolean,
    val dateUnlocked: String
)
