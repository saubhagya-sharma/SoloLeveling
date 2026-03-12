package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var workoutDaysTextView: TextView
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val workoutDates = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        calendarView = findViewById(R.id.calendar_workout_history)
        workoutDaysTextView = findViewById(R.id.text_workout_days_hint)

        loadWorkoutDates()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = formatDate(year, month, dayOfMonth)
            lifecycleScope.launch {
                val session = database.workoutSessionDao().getSessionByDateWithExercises(date)
                if (session != null) {
                    startActivity(
                        Intent(this@HistoryActivity, SessionDetailActivity::class.java).apply {
                            putExtra(SessionDetailActivity.EXTRA_SESSION_ID, session.id)
                            putExtra(SessionDetailActivity.EXTRA_DATE, session.date)
                        }
                    )
                }
            }
        }
    }

    private fun loadWorkoutDates() {
        lifecycleScope.launch {
            workoutDates.clear()
            workoutDates.addAll(database.workoutSessionDao().getWorkoutDatesWithExercises())
            workoutDaysTextView.text = if (workoutDates.isEmpty()) {
                "No completed workout days yet."
            } else {
                "Workout Days: ${workoutDates.sorted().joinToString(", ")}"
            }
        }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
}
