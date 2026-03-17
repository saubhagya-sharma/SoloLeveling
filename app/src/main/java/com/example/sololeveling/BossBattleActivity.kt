package com.example.sololeveling

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

class BossBattleActivity : AppCompatActivity() {

    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val formatter = DecimalFormat("0.##")

    private lateinit var bossNameText: TextView
    private lateinit var requirementText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var startLoggingButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boss_battle)

        bossNameText = findViewById(R.id.text_boss_name)
        requirementText = findViewById(R.id.text_boss_requirement)
        attemptsText = findViewById(R.id.text_attempts_left)
        startLoggingButton = findViewById(R.id.button_start_logging)

        startLoggingButton.setOnClickListener {
            lifecycleScope.launch {
                val boss = database.bossDao().getActiveBoss() ?: return@launch

                val sessionId = database.workoutSessionDao().insert(
                    WorkoutSessionEntity(
                        date = LocalDate.now().toString(),
                        isBossSession = true,
                        bossName = boss.bossName
                    )
                ).toInt()

                val workoutExerciseId = database.workoutExerciseDao().insert(
                    WorkoutExerciseEntity(
                        sessionId = sessionId,
                        exerciseId = boss.exerciseId
                    )
                ).toInt()

                startActivity(
                    ExerciseWorkoutActivity.createBossIntent(
                        context = this@BossBattleActivity,
                        workoutExerciseId = workoutExerciseId,
                        bossId = boss.id
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val boss = database.bossDao().getActiveBoss() ?: run {
                finish()
                return@launch
            }

            bossNameText.text = boss.bossName
            attemptsText.text = "Attempts Left: ${"❤️".repeat(boss.attemptsLeft.coerceAtLeast(0))}"
            requirementText.text = if (boss.requiredMinutes != null) {
                "${boss.exerciseName}\n${formatter.format(boss.requiredMinutes)} min\n3 sets"
            } else {
                val weight = formatter.format(boss.requiredWeight ?: 0.0)
                val reps = boss.requiredReps ?: 0
                "${boss.exerciseName}\n${weight}kg x $reps reps\n3 sets"
            }
        }
    }
}
