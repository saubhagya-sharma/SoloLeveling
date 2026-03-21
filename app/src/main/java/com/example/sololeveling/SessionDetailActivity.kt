package com.example.sololeveling

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.core.DailyQuestType
import com.example.sololeveling.data.local.DatabaseProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var dateTitleTextView: TextView
    private lateinit var exercisesRecyclerView: RecyclerView
    private lateinit var summaryTextView: TextView
    private lateinit var dailyQuestsTextView: TextView

    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val adapter = SessionExerciseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        val date = intent.getStringExtra(EXTRA_DATE).orEmpty()
        if (date.isBlank()) {
            finish()
            return
        }

        dateTitleTextView = findViewById(R.id.text_session_date_title)
        exercisesRecyclerView = findViewById(R.id.recycler_session_exercises)
        summaryTextView = findViewById(R.id.text_session_summary)
        dailyQuestsTextView = findViewById(R.id.text_daily_quests_history)

        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView.adapter = adapter

        lifecycleScope.launch {
            dateTitleTextView.text = "WORKOUT HISTORY - ${formatDateTitle(date)}"
            loadAllSessions(date)
        }
    }

    private suspend fun loadAllSessions(date: String) {
        val sessions = database.workoutSessionDao().getAllSessionsByDateWithExercises(date)

        val items = mutableListOf<SessionDetailListItem>()
        var totalExercises = 0
        var totalSets = 0
        var totalVolume = 0.0

        sessions.forEach { session ->
            val headerTitle = if (session.isBossSession && !session.bossName.isNullOrBlank()) {
                "🔴 BOSS: ${session.bossName}"
            } else {
                "WORKOUT"
            }

            items.add(SessionDetailListItem.ExerciseHeader(headerTitle))

            val exerciseDetails = database.workoutExerciseDao()
                .getSessionExerciseDetails(session.id)
            totalExercises += exerciseDetails.size

            exerciseDetails.forEach { exerciseDetail ->
                items.add(SessionDetailListItem.ExerciseHeader(exerciseDetail.exerciseName))

                val sets = database.workoutSetDao()
                    .getByWorkoutExerciseId(exerciseDetail.workoutExerciseId)

                sets.forEach { set ->
                    items.add(
                        SessionDetailListItem.SetRow(
                            reps = set.reps,
                            weight = set.weight,
                            minutes = set.minutes
                        )
                    )

                    totalSets += 1
                    if (!exerciseDetail.isTimeBased && set.weight != null && set.reps != null) {
                        totalVolume += set.weight * set.reps
                    }
                }
            }
        }

        adapter.submitItems(items)
        summaryTextView.text = "Exercises: $totalExercises\nSets: $totalSets\nVolume: ${totalVolume.toInt()} kg"

        val dailyQuests = database.dailyQuestDao().getQuestsForDate(date)
        dailyQuestsTextView.text = if (dailyQuests.isEmpty()) {
            "No daily quests for this date"
        } else {
            dailyQuests.joinToString("\n") { quest ->
                val icon = if (quest.completed) "✓" else "✗"
                "$icon ${DailyQuestType.fromName(quest.questType).displayText}"
            }
        }
    }

    private fun formatDateTitle(date: String): String {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
        return if (parsed != null) {
            SimpleDateFormat("MMMM dd", Locale.US).format(parsed)
        } else {
            date
        }
    }

    companion object {
        const val EXTRA_DATE = "extra_date"
    }
}
