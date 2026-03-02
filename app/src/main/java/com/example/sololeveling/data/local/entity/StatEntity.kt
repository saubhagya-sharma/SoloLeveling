package com.example.sololeveling.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stat")
data class StatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val level: Int,
    val currentXp: Double
)
