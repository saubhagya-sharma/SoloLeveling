package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String
)
