package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trophy")
data class TrophyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bossName: String,
    val exerciseName: String,
    val dateEarned: String
)
