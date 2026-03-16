package com.example.sololeveling.core

import com.example.sololeveling.data.local.AppDatabase
import com.example.sololeveling.data.local.entity.PlayerEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DisciplineManager(
    private val database: AppDatabase
) {
    suspend fun handleVisit(): VisitResult {
        val player = database.playerDao().getPlayer() ?: return VisitResult(0.0, false, false)
        val today = currentDate()

        val resetPlayer = weeklyReset(player, today)
        if (resetPlayer.lastVisitDate == today) {
            return VisitResult(0.0, false, false)
        }

        val updatedVisits = resetPlayer.weeklyVisits + 1
        val rewardXp = calculateRewards(updatedVisits, resetPlayer.weeklyGoalDays)

        val goalReached = updatedVisits == resetPlayer.weeklyGoalDays
        val updatedPlayer = resetPlayer.copy(
            weeklyVisits = updatedVisits,
            lastVisitDate = today,
            totalWeeklyGoalsCompleted = if (goalReached) {
                resetPlayer.totalWeeklyGoalsCompleted + 1
            } else {
                resetPlayer.totalWeeklyGoalsCompleted
            }
        )

        database.playerDao().updatePlayer(updatedPlayer)

        
        val extraDay = updatedVisits > resetPlayer.weeklyGoalDays

        return VisitResult(
            xp = rewardXp,
            goalReached = goalReached,
            extraDay = extraDay
        )
    }

    suspend fun weeklyReset(
        player: PlayerEntity,
        today: String = currentDate()
    ): PlayerEntity {
        val shouldReset = hasWeekChanged(player.lastWeekResetDate, today)
        if (!shouldReset) {
            return player
        }

        val resetPlayer = player.copy(
            weeklyVisits = 0,
            lastWeekResetDate = today
        )
        database.playerDao().updatePlayer(resetPlayer)
        return resetPlayer
    }

    fun calculateRewards(weeklyVisits: Int, weeklyGoalDays: Int): Double {
        var reward = 150.0
        if (weeklyVisits == weeklyGoalDays) {
            reward += 500.0
        } else if (weeklyVisits > weeklyGoalDays) {
            reward += 100.0
        }
        return reward
    }

    private fun hasWeekChanged(lastWeekResetDate: String?, today: String): Boolean {
        if (lastWeekResetDate.isNullOrBlank()) return true

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val lastDate = formatter.parse(lastWeekResetDate) ?: return true
        val todayDate = formatter.parse(today) ?: return true

        val lastCalendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            time = lastDate
        }
        val todayCalendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            time = todayDate
        }

        val sameWeek = lastCalendar.get(Calendar.WEEK_OF_YEAR) == todayCalendar.get(Calendar.WEEK_OF_YEAR)
        val sameYear = lastCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
        return !(sameWeek && sameYear)
    }

    private fun currentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
