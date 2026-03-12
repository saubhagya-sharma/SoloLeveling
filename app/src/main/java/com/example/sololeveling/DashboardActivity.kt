package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.example.sololeveling.BuildConfig
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity
import com.example.sololeveling.domain.StatType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    private lateinit var playerNameTextView: TextView
    private lateinit var overallLevelTextView: TextView
    private lateinit var strengthLevelTextView: TextView
    private lateinit var enduranceLevelTextView: TextView
    private lateinit var staminaLevelTextView: TextView
    private lateinit var disciplineLevelTextView: TextView
    private lateinit var weeklyGoalTextView: TextView
    private lateinit var editGoalButton: Button
    private lateinit var strengthProgressBar: ProgressBar
    private lateinit var enduranceProgressBar: ProgressBar
    private lateinit var staminaProgressBar: ProgressBar
    private lateinit var disciplineProgressBar: ProgressBar
    private lateinit var muscleContainer: LinearLayout
    private lateinit var chestLevelTextView: TextView
    private lateinit var chestProgressBar: ProgressBar
    private lateinit var backLevelTextView: TextView
    private lateinit var backProgressBar: ProgressBar
    private lateinit var legsLevelTextView: TextView
    private lateinit var legsProgressBar: ProgressBar
    private lateinit var shouldersLevelTextView: TextView
    private lateinit var shouldersProgressBar: ProgressBar
    private lateinit var armsLevelTextView: TextView
    private lateinit var armsProgressBar: ProgressBar
    private lateinit var coreLevelTextView: TextView
    private lateinit var coreProgressBar: ProgressBar
    private lateinit var startWorkoutButton: Button
    private lateinit var workoutHistoryButton: Button
    private lateinit var devResetButton: Button

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
        weeklyGoalTextView = findViewById(R.id.text_weekly_goal)
        editGoalButton = findViewById(R.id.button_edit_goal)
        strengthProgressBar = findViewById(R.id.progress_strength)
        enduranceProgressBar = findViewById(R.id.progress_endurance)
        staminaProgressBar = findViewById(R.id.progress_stamina)
        disciplineProgressBar = findViewById(R.id.progress_discipline)
        muscleContainer = findViewById(R.id.muscle_container)
        chestLevelTextView = findViewById(R.id.text_chest_level)
        chestProgressBar = findViewById(R.id.progress_chest)
        backLevelTextView = findViewById(R.id.text_back_level)
        backProgressBar = findViewById(R.id.progress_back)
        legsLevelTextView = findViewById(R.id.text_legs_level)
        legsProgressBar = findViewById(R.id.progress_legs)
        shouldersLevelTextView = findViewById(R.id.text_shoulders_level)
        shouldersProgressBar = findViewById(R.id.progress_shoulders)
        armsLevelTextView = findViewById(R.id.text_arms_level)
        armsProgressBar = findViewById(R.id.progress_arms)
        coreLevelTextView = findViewById(R.id.text_core_level)
        coreProgressBar = findViewById(R.id.progress_core)
        startWorkoutButton = findViewById(R.id.button_start_workout)
        workoutHistoryButton = findViewById(R.id.button_workout_history)
        devResetButton = findViewById(R.id.button_dev_reset)

        if (BuildConfig.DEBUG) {
            devResetButton.visibility = View.VISIBLE
        }

        editGoalButton.setOnClickListener {
            showEditGoalDialog()
        }

        refreshUi()

        devResetButton.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Developer Reset")
                .setMessage("This will delete ALL saved data and restart the app. Continue?")
                .setPositiveButton("RESET") { _, _ ->

                    lifecycleScope.launch {

                        val db = DatabaseProvider.getDatabase(this@DashboardActivity)

                        db.muscleStatDao().deleteAll()
                        db.statDao().deleteAll()
                        db.playerDao().deleteAll()

                        val intent = Intent(this@DashboardActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                        startActivity(intent)
                        finish()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        startWorkoutButton.setOnClickListener {
            lifecycleScope.launch {
                val sessionDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val existingSession = database.workoutSessionDao().getSessionByDate(sessionDate)
                val sessionId = existingSession?.id ?: database.workoutSessionDao().insert(
                    WorkoutSessionEntity(date = sessionDate)
                ).toInt()

                startActivity(
                    ExerciseSelectionActivity.createIntent(
                        context = this@DashboardActivity,
                        workoutSessionId = sessionId
                    )
                )
            }
        }

        workoutHistoryButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        refreshUi()
        maybeShowMuscleUnlockAchievement()
        maybeShowDisciplineAchievement()
    }

    private fun maybeShowMuscleUnlockAchievement() {
        val player = GameManager.player

        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer()
            if (playerEntity != null && !playerEntity.muscleUnlocked && player.overallLevel() >= 5) {
                AlertDialog.Builder(this@DashboardActivity)
                    .setTitle("Achievement Unlocked")
                    .setMessage("Congratulations ${player.name}. You have unlocked Muscle Stats.")
                    .setPositiveButton("Continue", null)
                    .show()

                database.playerDao().updateMuscleUnlocked(true)
            }
        }
    }

    private fun maybeShowDisciplineAchievement() {

        if (GameManager.pendingGoalCompletion) {
            AlertDialog.Builder(this)
                .setTitle("GOAL COMPLETE")
                .setMessage("+500 Discipline XP\nWeekly goal achieved!")
                .setPositiveButton("Continue", null)
                .show()

            GameManager.pendingGoalCompletion = false
        } else if (GameManager.pendingExtraWorkout) {
            AlertDialog.Builder(this)
                .setTitle("EXTRA WORKOUT")
                .setMessage("+100 Discipline XP\nYou trained beyond your weekly goal!")
                .setPositiveButton("Continue", null)
                .show()

            GameManager.pendingExtraWorkout = false
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

        if (player.isMuscleUnlocked()) {
            muscleContainer.visibility = View.VISIBLE

            val chest = player.getMuscle("Chest")
            val back = player.getMuscle("Back")
            val legs = player.getMuscle("Legs")
            val shoulders = player.getMuscle("Shoulders")
            val arms = player.getMuscle("Arms")
            val core = player.getMuscle("Core")

            chestLevelTextView.text = "Chest Lv. ${chest?.level ?: 0}"
            chestProgressBar.progress = ((chest?.progressToNextLevel() ?: 0.0) * 100).toInt()

            backLevelTextView.text = "Back Lv. ${back?.level ?: 0}"
            backProgressBar.progress = ((back?.progressToNextLevel() ?: 0.0) * 100).toInt()

            legsLevelTextView.text = "Legs Lv. ${legs?.level ?: 0}"
            legsProgressBar.progress = ((legs?.progressToNextLevel() ?: 0.0) * 100).toInt()

            shouldersLevelTextView.text = "Shoulders Lv. ${shoulders?.level ?: 0}"
            shouldersProgressBar.progress = ((shoulders?.progressToNextLevel() ?: 0.0) * 100).toInt()

            armsLevelTextView.text = "Arms Lv. ${arms?.level ?: 0}"
            armsProgressBar.progress = ((arms?.progressToNextLevel() ?: 0.0) * 100).toInt()

            coreLevelTextView.text = "Core Lv. ${core?.level ?: 0}"
            coreProgressBar.progress = ((core?.progressToNextLevel() ?: 0.0) * 100).toInt()
        } else {
            muscleContainer.visibility = View.GONE
        }

        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer() ?: return@launch
            weeklyGoalTextView.text = "Weekly Goal: ${playerEntity.weeklyVisits} / ${playerEntity.weeklyGoalDays}"
        }
    }

    private fun showEditGoalDialog() {
        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer() ?: return@launch

            val numberPicker = NumberPicker(this@DashboardActivity).apply {
                minValue = 1
                maxValue = 7
                value = playerEntity.weeklyGoalDays.coerceIn(1, 7)
            }

            AlertDialog.Builder(this@DashboardActivity)
                .setTitle("Edit Weekly Goal")
                .setView(numberPicker)
                .setPositiveButton("Save") { _, _ ->
                    lifecycleScope.launch {
                        database.playerDao().updatePlayer(
                            playerEntity.copy(weeklyGoalDays = numberPicker.value)
                        )
                        refreshUi()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
