package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_pr")
data class ExercisePrEntity(
    @PrimaryKey val exerciseId: Int,
    val prWeight: Double,
    val prReps: Int,
    val dateAchieved: String
)
