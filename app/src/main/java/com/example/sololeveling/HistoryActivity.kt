package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.CalendarView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var workoutDaysTextView: TextView
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private var workoutIndicators = emptyMap<LocalDate, DayMarker>()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        calendarView = findViewById(R.id.calendar_workout_history)
        workoutDaysTextView = findViewById(R.id.text_workout_days_hint)

        setupCalendar()
        loadWorkoutDates()
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)

        calendarView.dayBinder = object : MonthDayBinder<WorkoutDayContainer> {
            override fun create(view: View): WorkoutDayContainer = WorkoutDayContainer(view)

            override fun bind(container: WorkoutDayContainer, data: CalendarDay) {
                container.day = data
                container.dayTextView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.dayTextView.alpha = 1f
                    val marker = workoutIndicators[data.date]
                    if (marker != null) {
                        container.indicatorTextView.visibility = View.VISIBLE
                        container.indicatorTextView.text = marker.toIndicator()
                    } else {
                        container.indicatorTextView.visibility = View.INVISIBLE
                    }
                } else {
                    container.dayTextView.alpha = 0.3f
                    container.indicatorTextView.visibility = View.INVISIBLE
                }
            }
        }

        calendarView.setup(startMonth, endMonth, java.time.DayOfWeek.MONDAY)
        calendarView.scrollToMonth(currentMonth)
    }

    private fun loadWorkoutDates() {
        lifecycleScope.launch {
            val entries = database.workoutSessionDao().getWorkoutDateEntries()
            workoutIndicators = entries.mapNotNull {
                val date = runCatching { LocalDate.parse(it.date, dateFormatter) }.getOrNull() ?: return@mapNotNull null
                date to DayMarker(hasWorkout = !it.isBossSession, hasBoss = it.isBossSession)
            }.groupBy({ it.first }, { it.second }).mapValues { (_, markers) ->
                DayMarker(
                    hasWorkout = markers.any { it.hasWorkout },
                    hasBoss = markers.any { it.hasBoss }
                )
            }

            workoutDaysTextView.text = if (entries.isEmpty()) {
                "No completed workout days yet."
            } else {
                "Workout Days: ${entries.map { it.date }.distinct().sorted().joinToString(", ")}"
            }

            calendarView.notifyCalendarChanged()
        }
    }

    private inner class WorkoutDayContainer(view: View) : ViewContainer(view) {
        val dayTextView: TextView = view.findViewById(R.id.text_day)
        val indicatorTextView: TextView = view.findViewById(R.id.text_indicator)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                if (!::day.isInitialized || day.position != DayPosition.MonthDate) return@setOnClickListener
                val selectedDate = day.date.format(dateFormatter)
                lifecycleScope.launch {
                    val sessions = database.workoutSessionDao()
                        .getAllSessionsByDateWithExercises(selectedDate)

                    if (sessions.isNotEmpty()) {
                        startActivity(
                            Intent(this@HistoryActivity, SessionDetailActivity::class.java).apply {
                                putExtra(SessionDetailActivity.EXTRA_DATE, selectedDate)
                            }
                        )
                    }
                }
            }
        }
    }

    private data class DayMarker(
        val hasWorkout: Boolean,
        val hasBoss: Boolean
    ) {
        fun toIndicator(): String {
            return when {
                hasWorkout && hasBoss -> "🟢🔴"
                hasBoss -> "🔴"
                hasWorkout -> "🟢"
                else -> ""
            }
        }
    }
}
