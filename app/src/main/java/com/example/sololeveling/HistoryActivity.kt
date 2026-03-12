package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var workoutDaysTextView: TextView
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val workoutDates = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        calendarView = findViewById(R.id.calendar_workout_history)
        workoutDaysTextView = findViewById(R.id.text_workout_days_hint)

        loadWorkoutDates()

        calendarView.setOnDateChangedListener { _, date, _ ->
            val formattedDate = String.format(
                "%04d-%02d-%02d",
                date.year,
                date.month + 1,
                date.day
            )

            lifecycleScope.launch {
                val session = database.workoutSessionDao()
                    .getSessionByDateWithExercises(formattedDate)

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

            val decoratedDays = workoutDates.map {
                val parts = it.split("-")
                CalendarDay.from(
                    parts[0].toInt(),
                    parts[1].toInt() - 1,
                    parts[2].toInt()
                )
            }

            calendarView.addDecorator(WorkoutDayDecorator(decoratedDays))

            workoutDaysTextView.text = if (workoutDates.isEmpty()) {
                "No completed workout days yet."
            } else {
                "Workout Days: ${workoutDates.sorted().joinToString(", ")}"
            }
        }
    }
}
