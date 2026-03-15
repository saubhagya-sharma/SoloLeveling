package com.example.sololeveling

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.DailyQuestManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.domain.StatType
import kotlinx.coroutines.launch

class DailyQuestActivity : AppCompatActivity() {

    private lateinit var questsContainer: LinearLayout
    private lateinit var progressText: TextView

    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val questManager by lazy { DailyQuestManager(this, database) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_quests)

        questsContainer = findViewById(R.id.container_daily_quests)
        progressText = findViewById(R.id.text_daily_quest_progress)

        loadDailyQuests()
    }

    private fun loadDailyQuests() {
        lifecycleScope.launch {
            val quests = questManager.getTodayQuests()
            renderQuestRows(quests)
            refreshProgress(quests.count { it.completed })
        }
    }

    private fun renderQuestRows(quests: List<DailyQuestManager.DailyQuestUiModel>) {
        questsContainer.removeAllViews()

        quests.forEach { quest ->
            val checkBox = CheckBox(this).apply {
                text = quest.displayText
                isChecked = quest.completed
                textSize = 18f
                setOnCheckedChangeListener { _, isChecked ->
                    lifecycleScope.launch {
                        val result = questManager.updateQuestCompletion(quest.id, isChecked) ?: return@launch
                        refreshProgress(result.completedCount)

                        if (isChecked) {
                            val popupMessage = buildString {
                                append("QUEST COMPLETED\n")
                                append(result.questDisplayText)
                                append("\n")
                                append("${result.completedCount}/${result.totalCount} quests completed")
                            }
                            SystemMessageManager.show(this@DailyQuestActivity, popupMessage)
                        }

                        if (result.rewardGranted) {
                            persistDisciplineStat()
                            SystemMessageManager.show(
                                this@DailyQuestActivity,
                                "DAILY QUESTS COMPLETED\n+150 Discipline XP"
                            )
                        }
                    }
                }
            }
            questsContainer.addView(checkBox)
        }
    }

    private suspend fun persistDisciplineStat() {
        val disciplineStat = com.example.sololeveling.core.GameManager.player.getStat(StatType.DISCIPLINE) ?: return
        database.statDao().updateStat(
            type = StatType.DISCIPLINE.name,
            level = disciplineStat.level,
            currentXp = disciplineStat.currentXp
        )
    }

    private fun refreshProgress(completedCount: Int) {
        progressText.text = "Progress: $completedCount/3"
    }
}
