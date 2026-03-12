package com.example.sololeveling

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.data.local.DatabaseProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var dateTitleTextView: TextView
    private lateinit var exercisesRecyclerView: RecyclerView
    private lateinit var summaryTextView: TextView

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

        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView.adapter = adapter

        dateTitleTextView.text = "WORKOUT - ${formatDateTitle(date)}"

        loadSessionDetails(sessionId)
    }

    private fun loadSessionDetails(sessionId: Int) {
        lifecycleScope.launch {
            val exerciseDetails = database.workoutExerciseDao().getSessionExerciseDetails(sessionId)

            val items = mutableListOf<SessionDetailListItem>()
            var totalSets = 0
            var totalVolume = 0.0

            exerciseDetails.forEach { exerciseDetail ->
                items.add(SessionDetailListItem.ExerciseHeader(exerciseDetail.exerciseName))
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
