package com.example.sololeveling

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.core.DailyQuestType
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.WorkoutSessionEntity
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

        val sessionId = intent.getIntExtra(EXTRA_SESSION_ID, INVALID_ID)
        val date = intent.getStringExtra(EXTRA_DATE).orEmpty()
        if (sessionId == INVALID_ID || date.isBlank()) {
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
            val session = database.workoutSessionDao().getSessionById(sessionId)
            dateTitleTextView.text = buildSessionTitle(session, date)
            loadSessionDetails(sessionId, date, session)
        }
    }

    private suspend fun loadSessionDetails(sessionId: Int, date: String, session: WorkoutSessionEntity?) {
        val exerciseDetails = database.workoutExerciseDao().getSessionExerciseDetails(sessionId)

        val items = mutableListOf<SessionDetailListItem>()
        var totalSets = 0
        var totalVolume = 0.0

        exerciseDetails.forEach { exerciseDetail ->
            val exerciseTitle = if (session?.isBossSession == true) {
                "🔴 ${exerciseDetail.exerciseName}"
            } else {
                exerciseDetail.exerciseName
            }
            items.add(SessionDetailListItem.ExerciseHeader(exerciseTitle))
            val sets = database.workoutSetDao().getByWorkoutExerciseId(exerciseDetail.workoutExerciseId)
            sets.forEach { set ->
                items.add(
                    SessionDetailListItem.SetRow(
                        reps = set.reps,
                        weight = set.weight,
                        minutes = set.minutes
                    )
                )

                totalSets += 1
                if (!exerciseDetail.isTimeBased) {
                    if (set.weight != null && set.reps != null) {
                        totalVolume += set.weight * set.reps
                    }
                }
            }
        }

        adapter.submitItems(items)
        summaryTextView.text = "Exercises: ${exerciseDetails.size}\nSets: $totalSets\nVolume: ${totalVolume.toInt()} kg"

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

    private fun buildSessionTitle(session: WorkoutSessionEntity?, date: String): String {
        return if (session?.isBossSession == true && !session.bossName.isNullOrBlank()) {
            "🔴 BOSS: ${session.bossName} - ${formatDateTitle(date)}"
        } else {
            "WORKOUT - ${formatDateTitle(date)}"
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
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_DATE = "extra_date"
        private const val INVALID_ID = -1
    }
}
