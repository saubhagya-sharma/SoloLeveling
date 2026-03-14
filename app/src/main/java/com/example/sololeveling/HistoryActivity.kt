package com.example.sololeveling

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.ViewContainer
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.DayBinder
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var workoutDaysTextView: TextView
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private var workoutDates = emptySet<LocalDate>()
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

        calendarView.dayBinder = object : DayBinder<WorkoutDayContainer> {
            override fun create(view: View): WorkoutDayContainer = WorkoutDayContainer(view)

            override fun bind(container: WorkoutDayContainer, data: CalendarDay) {
                container.day = data
                container.dayTextView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.dayTextView.alpha = 1f
                    if (data.date in workoutDates) {
                        container.dotView.visibility = View.VISIBLE
                        container.dotView.background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(Color.GREEN)
                        }
                    } else {
                        container.dotView.visibility = View.INVISIBLE
                    }
                } else {
                    container.dayTextView.alpha = 0.3f
                    container.dotView.visibility = View.INVISIBLE
                }
            }
        }

        calendarView.setup(startMonth, endMonth, java.time.DayOfWeek.MONDAY)
        calendarView.scrollToMonth(currentMonth)
    }

    private fun loadWorkoutDates() {
        lifecycleScope.launch {
            val rawDates = database.workoutSessionDao().getWorkoutDatesWithExercises()
            workoutDates = rawDates.mapNotNull {
                runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
            }.toSet()

            workoutDaysTextView.text = if (rawDates.isEmpty()) {
                "No completed workout days yet."
            } else {
                "Workout Days: ${rawDates.sorted().joinToString(", ")}"
            }

            calendarView.notifyCalendarChanged()
        }
    }

    private inner class WorkoutDayContainer(view: View) : ViewContainer(view) {
        val dayTextView: TextView = view.findViewById(R.id.text_day)
        val dotView: View = view.findViewById(R.id.view_workout_dot)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                if (!::day.isInitialized || day.position != DayPosition.MonthDate) return@setOnClickListener
                val selectedDate = day.date.format(dateFormatter)
                lifecycleScope.launch {
                    val session = database.workoutSessionDao()
                        .getSessionByDateWithExercises(selectedDate)

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
    }
}
