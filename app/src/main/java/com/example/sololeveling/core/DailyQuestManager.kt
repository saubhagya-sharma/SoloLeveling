package com.example.sololeveling.core

import android.content.Context
import com.example.sololeveling.data.local.AppDatabase
import com.example.sololeveling.data.local.entity.DailyQuestEntity
import com.example.sololeveling.domain.StatType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyQuestManager(
    private val context: Context,
    private val database: AppDatabase
) {

    data class DailyQuestUiModel(
        val id: Int,
        val questType: String,
        val displayText: String,
        val completed: Boolean
    )

    data class DailyQuestCompletionResult(
        val questDisplayText: String,
        val completedCount: Int,
        val totalCount: Int,
        val rewardGranted: Boolean
    )

    private val dao = database.dailyQuestDao()
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun ensureDailyQuests() {
        val today = currentDate()
        dao.clearOldQuests(today)

        val existing = dao.getQuestsForDate(today)
        if (existing.isNotEmpty()) {
            return
        }

        val quests = DailyQuestType.entries
            .shuffled()
            .take(DAILY_QUEST_COUNT)
            .map { type ->
                DailyQuestEntity(
                    date = today,
                    questType = type.name,
                    completed = false
                )
            }

        dao.insertAll(quests)
    }

    suspend fun getQuestsForDate(date: String): List<DailyQuestUiModel> {
        return dao.getQuestsForDate(date)
            .map { it.toUiModel() }
    }

    suspend fun getTodayQuests(): List<DailyQuestUiModel> {
        ensureDailyQuests()
        return getQuestsForDate(currentDate())
    }

    suspend fun updateQuestCompletion(questId: Int, completed: Boolean): DailyQuestCompletionResult? {
        ensureDailyQuests()
        val today = currentDate()
        val quests = dao.getQuestsForDate(today)
        val quest = quests.firstOrNull { it.id == questId } ?: return null

        val updatedQuest = quest.copy(completed = completed)
        dao.updateQuest(updatedQuest)

        if (!quest.completed && completed) {
            val player = database.playerDao().getPlayer()
            if (player != null) {
                database.playerDao().updatePlayer(
                    player.copy(totalQuestCompletions = player.totalQuestCompletions + 1)
                )
            }
        }

        val updatedQuests = dao.getQuestsForDate(today)
        val completedCount = updatedQuests.count { it.completed }

        val shouldReward = completedCount == DAILY_QUEST_COUNT && !isTodayRewardAlreadyGranted(today)
        if (shouldReward) {
            GameManager.player.getStat(StatType.DISCIPLINE)?.addXp(DAILY_REWARD_XP)
            markTodayRewardGranted(today)
        }

        return DailyQuestCompletionResult(
            questDisplayText = DailyQuestType.fromName(updatedQuest.questType).displayText,
            completedCount = completedCount,
            totalCount = DAILY_QUEST_COUNT,
            rewardGranted = shouldReward
        )
    }

    private fun DailyQuestEntity.toUiModel(): DailyQuestUiModel {
        return DailyQuestUiModel(
            id = id,
            questType = questType,
            displayText = DailyQuestType.fromName(questType).displayText,
            completed = completed
        )
    }

    private fun currentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun isTodayRewardAlreadyGranted(today: String): Boolean {
        return prefs.getString(KEY_REWARDED_DATE, null) == today
    }

    private fun markTodayRewardGranted(today: String) {
        prefs.edit().putString(KEY_REWARDED_DATE, today).apply()
    }

    companion object {
        const val DAILY_REWARD_XP = 150.0
        private const val DAILY_QUEST_COUNT = 3
        private const val PREFS_NAME = "daily_quest_prefs"
        private const val KEY_REWARDED_DATE = "rewarded_date"
    }
}

enum class DailyQuestType(val displayText: String) {
    SITUPS_15("Perform 15 Sit-ups"),
    PUSHUPS_10("Perform 10 Push-ups"),
    JUMPING_JACKS_30("Perform 30 Jumping Jacks"),
    SQUATS_20("Perform 20 Squats"),
    PLANK_30S("Hold a 30s Plank"),
    HIGH_KNEES_30S("High Knees for 30s"),
    WALK_4000_STEPS("Walk 4000 Steps"),
    LUNGES_20("Perform 20 Lunges"),
    CALF_RAISES_30("Perform 30 Calf Raises"),
    WALL_SIT_30S("Hold a 30s Wall Sit");

    companion object {
        fun fromName(name: String): DailyQuestType {
            return entries.firstOrNull { it.name == name } ?: SQUATS_20
        }
    }
}
