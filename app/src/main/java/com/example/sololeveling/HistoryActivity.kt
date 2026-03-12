package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
<<<<<<< codex/implement-workout-history-feature
=======
import android.widget.CalendarView
>>>>>>> master
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
<<<<<<< codex/implement-workout-history-feature
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
=======
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
>>>>>>> master
    private lateinit var workoutDaysTextView: TextView
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val workoutDates = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        calendarView = findViewById(R.id.calendar_workout_history)
        workoutDaysTextView = findViewById(R.id.text_workout_days_hint)

        loadWorkoutDates()

<<<<<<< codex/implement-workout-history-feature
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

=======
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = formatDate(year, month, dayOfMonth)
            lifecycleScope.launch {
                val session = database.workoutSessionDao().getSessionByDateWithExercises(date)
>>>>>>> master
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
<<<<<<< codex/implement-workout-history-feature

            val decoratedDays = workoutDates.map {
                val parts = it.split("-")
                CalendarDay.from(
                    parts[0].toInt(),
                    parts[1].toInt() - 1,
                    parts[2].toInt()
                )
            }

            calendarView.addDecorator(WorkoutDayDecorator(decoratedDays))

=======
>>>>>>> master
            workoutDaysTextView.text = if (workoutDates.isEmpty()) {
                "No completed workout days yet."
            } else {
                "Workout Days: ${workoutDates.sorted().joinToString(", ")}"
            }
        }
    }
<<<<<<< codex/implement-workout-history-feature
=======

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
>>>>>>> master
}
