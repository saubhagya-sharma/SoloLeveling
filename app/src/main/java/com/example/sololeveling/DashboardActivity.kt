package com.example.sololeveling

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.domain.Exercise
import com.example.sololeveling.domain.StatType
import com.example.sololeveling.domain.WorkoutProcessor
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var playerNameTextView: TextView
    private lateinit var overallLevelTextView: TextView
    private lateinit var strengthLevelTextView: TextView
    private lateinit var enduranceLevelTextView: TextView
    private lateinit var staminaLevelTextView: TextView
    private lateinit var disciplineLevelTextView: TextView
    private lateinit var strengthProgressBar: ProgressBar
    private lateinit var enduranceProgressBar: ProgressBar
    private lateinit var staminaProgressBar: ProgressBar
    private lateinit var disciplineProgressBar: ProgressBar
    private lateinit var simulateWorkoutButton: Button

    private val workoutProcessor = WorkoutProcessor()
    private val database by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        playerNameTextView = findViewById(R.id.text_player_name)
        overallLevelTextView = findViewById(R.id.text_overall_level)
        strengthLevelTextView = findViewById(R.id.text_strength_level)
        enduranceLevelTextView = findViewById(R.id.text_endurance_level)
        staminaLevelTextView = findViewById(R.id.text_stamina_level)
        disciplineLevelTextView = findViewById(R.id.text_discipline_level)
        strengthProgressBar = findViewById(R.id.progress_strength)
        enduranceProgressBar = findViewById(R.id.progress_endurance)
        staminaProgressBar = findViewById(R.id.progress_stamina)
        disciplineProgressBar = findViewById(R.id.progress_discipline)
        simulateWorkoutButton = findViewById(R.id.button_simulate_workout)

        refreshUi()

        simulateWorkoutButton.setOnClickListener {
            val exercise = Exercise(
                name = "Bench Press",
                isTimeBased = false,
                baseWeight = 10.0,
                strengthMultiplier = 5.0,
                enduranceMultiplier = 5.0,
                staminaMultiplier = 5.0
            )

            workoutProcessor.processRepWorkout(
                exercise = exercise,
                reps = 20,
                weight = 20.0,
                stats = GameManager.player.stats
            )

            lifecycleScope.launch {
                GameManager.player.stats.forEach { stat ->
                    val statDao = database.statDao()
                    val existing = statDao.getByType(stat.type.name)

                    if (existing == null) {
                        statDao.insertAll(
                            listOf(
                                StatEntity(
                                    type = stat.type.name,
                                    level = stat.level,
                                    currentXp = stat.currentXp
                                )
                            )
                        )
                    } else {
                        statDao.updateStat(
                            type = stat.type.name,
                            level = stat.level,
                            currentXp = stat.currentXp
                        )
                    }
                }
            }

            refreshUi()
            maybeShowMuscleUnlockAchievement()
        }
    }

    private fun maybeShowMuscleUnlockAchievement() {
        val player = GameManager.player
        if (player.overallLevel() >= 5 && !GameManager.hasMuscleUnlockBeenAnnounced) {
            AlertDialog.Builder(this)
                .setTitle("Achievement Unlocked")
                .setMessage("Congratulations ${player.name}. You have unlocked Muscle Stats.")
                .setPositiveButton("Continue", null)
                .show()

            GameManager.hasMuscleUnlockBeenAnnounced = true
        }
    }

    private fun refreshUi() {
        val player = GameManager.player

        playerNameTextView.text = getString(R.string.player_name_format, player.name)
        overallLevelTextView.text = getString(R.string.overall_level_format, player.overallLevel())

        val strengthStat = player.getStat(StatType.STRENGTH)
        val enduranceStat = player.getStat(StatType.ENDURANCE)
        val staminaStat = player.getStat(StatType.STAMINA)
        val disciplineStat = player.getStat(StatType.DISCIPLINE)

        val strengthLevel = strengthStat?.level ?: 0
        val enduranceLevel = enduranceStat?.level ?: 0
        val staminaLevel = staminaStat?.level ?: 0
        val disciplineLevel = disciplineStat?.level ?: 0

        strengthLevelTextView.text = getString(R.string.strength_level_format, strengthLevel)
        enduranceLevelTextView.text = getString(R.string.endurance_level_format, enduranceLevel)
        staminaLevelTextView.text = getString(R.string.stamina_level_format, staminaLevel)
        disciplineLevelTextView.text = getString(R.string.discipline_level_format, disciplineLevel)

        strengthProgressBar.progress = ((strengthStat?.progressToNextLevel() ?: 0.0) * 100).toInt()
        enduranceProgressBar.progress = ((enduranceStat?.progressToNextLevel() ?: 0.0) * 100).toInt()
        staminaProgressBar.progress = ((staminaStat?.progressToNextLevel() ?: 0.0) * 100).toInt()
        disciplineProgressBar.progress = ((disciplineStat?.progressToNextLevel() ?: 0.0) * 100).toInt()
    }
}
