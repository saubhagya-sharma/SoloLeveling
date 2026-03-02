package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isTimeBased: Boolean,
    val baseWeight: Double?,
    val strengthMultiplier: Double,
    val enduranceMultiplier: Double,
    val staminaMultiplier: Double,
    val primaryMuscleName: String
)
