package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boss")
data class BossEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: Int,
    val exerciseName: String,
    val bossName: String,
    val requiredWeight: Double?,
    val requiredReps: Int?,
    val requiredMinutes: Double?,
    val attemptsLeft: Int = 3,
    val createdDate: String,
    val expiryDate: String,
    val isCompleted: Boolean = false
)
