package com.example.sololeveling

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.domain.Exercise
import com.example.sololeveling.domain.StatType
import com.example.sololeveling.domain.WorkoutProcessor

class DashboardActivity : AppCompatActivity() {
    private lateinit var playerNameTextView: TextView
    private lateinit var overallLevelTextView: TextView
    private lateinit var strengthLevelTextView: TextView
    private lateinit var enduranceLevelTextView: TextView
    private lateinit var staminaLevelTextView: TextView
    private lateinit var disciplineLevelTextView: TextView
    private lateinit var simulateWorkoutButton: Button

    private val workoutProcessor = WorkoutProcessor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        playerNameTextView = findViewById(R.id.text_player_name)
        overallLevelTextView = findViewById(R.id.text_overall_level)
        strengthLevelTextView = findViewById(R.id.text_strength_level)
        enduranceLevelTextView = findViewById(R.id.text_endurance_level)
        staminaLevelTextView = findViewById(R.id.text_stamina_level)
        disciplineLevelTextView = findViewById(R.id.text_discipline_level)
        simulateWorkoutButton = findViewById(R.id.button_simulate_workout)

        refreshUi()

        simulateWorkoutButton.setOnClickListener {
            val exercise = Exercise(
                name = "Bench Press",
                isTimeBased = false,
                baseWeight = 10.0,
                strengthMultiplier = 3.0,
                enduranceMultiplier = 1.0,
                staminaMultiplier = 0.0
            )

            workoutProcessor.processRepWorkout(
                exercise = exercise,
                reps = 20,
                weight = 20.0,
                stats = GameManager.player.stats
            )

            refreshUi()
        }
    }

    private fun refreshUi() {
        val player = GameManager.player

        playerNameTextView.text = getString(R.string.player_name_format, player.name)
        overallLevelTextView.text = getString(R.string.overall_level_format, player.overallLevel())

        val strength = player.getStat(StatType.STRENGTH)?.level ?: 0
        val endurance = player.getStat(StatType.ENDURANCE)?.level ?: 0
        val stamina = player.getStat(StatType.STAMINA)?.level ?: 0
        val discipline = player.getStat(StatType.DISCIPLINE)?.level ?: 0

        strengthLevelTextView.text = getString(R.string.strength_level_format, strength)
        enduranceLevelTextView.text = getString(R.string.endurance_level_format, endurance)
        staminaLevelTextView.text = getString(R.string.stamina_level_format, stamina)
        disciplineLevelTextView.text = getString(R.string.discipline_level_format, discipline)
    }
}
