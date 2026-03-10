package com.example.sololeveling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import kotlinx.coroutines.launch

class SetGoalActivity : AppCompatActivity() {
    private val database by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goal)

        val goalNumberPicker: NumberPicker = findViewById(R.id.number_picker_goal)
        val saveGoalButton: Button = findViewById(R.id.button_save_goal)

        goalNumberPicker.minValue = 1
        goalNumberPicker.maxValue = 7

        lifecycleScope.launch {
            val player = database.playerDao().getPlayer() ?: return@launch
            goalNumberPicker.value = player.weeklyGoalDays.coerceIn(1, 7)
        }

        saveGoalButton.setOnClickListener {
            val selectedGoal = goalNumberPicker.value

            lifecycleScope.launch {
                val player = database.playerDao().getPlayer() ?: return@launch
                database.playerDao().updatePlayer(player.copy(weeklyGoalDays = selectedGoal))

                startActivity(Intent(this@SetGoalActivity, DashboardActivity::class.java))
                finish()
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SetGoalActivity::class.java)
        }
    }
}
