package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.AchievementManager
import com.example.sololeveling.core.BossManager
import com.example.sololeveling.core.DailyQuestManager
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
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
    private lateinit var cheatMealsTextView: TextView
    private lateinit var editGoalButton: Button
    private lateinit var redeemCheatMealButton: Button
    private lateinit var convertCheatMealButton: Button
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
    private lateinit var dailyQuestsButton: Button
    private lateinit var inventoryButton: Button
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
        cheatMealsTextView = findViewById(R.id.text_cheat_meals)
        editGoalButton = findViewById(R.id.button_edit_goal)
        redeemCheatMealButton = findViewById(R.id.button_redeem_cheat_meal)
        convertCheatMealButton = findViewById(R.id.button_convert_cheat_meal)
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
        dailyQuestsButton = findViewById(R.id.button_daily_quests)
        inventoryButton = findViewById(R.id.button_inventory)
        devResetButton = findViewById(R.id.button_dev_reset)

        if (BuildConfig.DEBUG) {
            devResetButton.visibility = View.VISIBLE
        }

        editGoalButton.setOnClickListener { showEditGoalDialog() }
        redeemCheatMealButton.setOnClickListener { redeemCheatMeal() }
        convertCheatMealButton.setOnClickListener { convertCheatMealToXp() }

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

        dailyQuestsButton.setOnClickListener {
            startActivity(Intent(this, DailyQuestActivity::class.java))
        }

        inventoryButton.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        AchievementManager.attachHost(this)

        refreshUi()
        refreshDailyQuestProgress()
        maybeShowMuscleUnlockAchievement()
        maybeShowDisciplineAchievement()

        lifecycleScope.launch {
            BossManager.deleteExpiredItems(database)
            val totalWorkouts = database.workoutSessionDao().countCompletedWorkouts()
            BossManager.maybeGiveRuneStone(database, totalWorkouts)
            AchievementManager.checkAchievements(database)
            refreshUi()
        }
    }

    private fun redeemCheatMeal() {
        lifecycleScope.launch {
            val player = database.playerDao().getPlayer() ?: return@launch
            if (player.cheatMeals <= 0) {
                SystemMessageManager.show(this@DashboardActivity, "NO CHEAT MEALS AVAILABLE")
                return@launch
            }

            database.playerDao().updatePlayer(player.copy(cheatMeals = player.cheatMeals - 1))
            SystemMessageManager.show(this@DashboardActivity, "CHEAT MEAL REDEEMED")
            refreshUi()
        }
    }

    private fun convertCheatMealToXp() {
        lifecycleScope.launch {
            val player = database.playerDao().getPlayer() ?: return@launch
            if (player.cheatMeals <= 0) {
                SystemMessageManager.show(this@DashboardActivity, "NO CHEAT MEALS AVAILABLE")
                return@launch
            }

            val options = arrayOf("Strength", "Endurance", "Stamina", "Discipline")
            AlertDialog.Builder(this@DashboardActivity)
                .setTitle("Convert Cheat Meal to XP")
                .setItems(options) { _, which ->
                    lifecycleScope.launch {
                        val statType = when (which) {
                            0 -> StatType.STRENGTH
                            1 -> StatType.ENDURANCE
                            2 -> StatType.STAMINA
                            else -> StatType.DISCIPLINE
                        }

                        val stat = GameManager.player.getStat(statType) ?: return@launch
                        stat.addXp(200.0)
                        database.statDao().updateStat(statType.name, stat.level, stat.currentXp)

                        val latestPlayer = database.playerDao().getPlayer() ?: return@launch
                        database.playerDao().updatePlayer(
                            latestPlayer.copy(cheatMeals = (latestPlayer.cheatMeals - 1).coerceAtLeast(0))
                        )

                        SystemMessageManager.show(
                            this@DashboardActivity,
                            "CHEAT MEAL CONVERTED\n+200 XP to ${options[which]}"
                        )
                        refreshUi()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun maybeShowMuscleUnlockAchievement() {
        val player = GameManager.player

        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer()
            if (playerEntity != null && !playerEntity.muscleUnlocked && player.overallLevel() >= 5) {
                SystemMessageManager.show(
                    this@DashboardActivity,
                    "ACHIEVEMENT\nMuscle Stats Unlocked\nCongratulations ${player.name}"
                )

                database.playerDao().updateMuscleUnlocked(true)
            }
        }
    }

    private fun maybeShowDisciplineAchievement() {

        if (GameManager.pendingGoalCompletion) {
            SystemMessageManager.show(
                this,
                "DISCIPLINE GOAL COMPLETE\n+500 Discipline XP\nWeekly goal achieved!"
            )

            GameManager.pendingGoalCompletion = false
        } else if (GameManager.pendingExtraWorkout) {
            SystemMessageManager.show(
                this,
                "ACHIEVEMENT\nEXTRA WORKOUT\n+100 Discipline XP"
            )

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
            cheatMealsTextView.text = "Cheat Meals: ${playerEntity.cheatMeals}"
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

    private fun refreshDailyQuestProgress() {
        lifecycleScope.launch {
            val quests = DailyQuestManager(this@DashboardActivity, database).getTodayQuests()
            val completed = quests.count { it.completed }
            dailyQuestsButton.text = "DAILY QUESTS ($completed/3)"
        }
    }
}
