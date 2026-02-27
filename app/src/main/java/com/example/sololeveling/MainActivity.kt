package com.example.sololeveling

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sololeveling.domain.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ===============================
        // 🔥 TESTING SOLO LEVELING ENGINE
        // ===============================

        // Create stats
        val strength = Stat(StatType.STRENGTH)
        val endurance = Stat(StatType.ENDURANCE)
        val stamina = Stat(StatType.STAMINA)

        val stats = listOf(strength, endurance, stamina)

        // Create a rep-based exercise
        val benchPress = Exercise(
            name = "Bench Press",
            isTimeBased = false,
            baseWeight = 10.0,
            strengthMultiplier = 3.0,
            enduranceMultiplier = 1.0,
            staminaMultiplier = 0.0
        )

        val processor = WorkoutProcessor()

        // Simulate workout
        processor.processRepWorkout(
            exercise = benchPress,
            reps = 10,
            weight = 20.0,
            stats = stats
        )

        // Log results
        for (stat in stats) {
            Log.d(
                "LEVEL_UP_TEST",
                "${stat.type} -> Level: ${stat.level}, XP: ${stat.currentXp}"
            )
        }
    }
}