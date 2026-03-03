package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.domain.PlayerProfile
import com.example.sololeveling.domain.MuscleStat
import com.example.sololeveling.domain.StatType
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)

        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer()

            if (playerEntity != null) {

                val statEntities = database.statDao().getAll()
                val statsByType = statEntities.associateBy { it.type }

                val restoredStats = StatType.entries.map { type ->
                    val statEntity = statsByType[type.name]
                    com.example.sololeveling.domain.Stat(
                        type = type,
                        level = statEntity?.level ?: 1,
                        currentXp = statEntity?.currentXp ?: 0.0
                    )
                }

                val baseMuscleList = listOf(
                    MuscleStat(name = "Chest"),
                    MuscleStat(name = "Back"),
                    MuscleStat(name = "Legs"),
                    MuscleStat(name = "Shoulders"),
                    MuscleStat(name = "Arms"),
                    MuscleStat(name = "Core")
                )

                GameManager.player = PlayerProfile(
                    name = playerEntity.name,
                    stats = restoredStats,
                    muscleStats = baseMuscleList
                )

                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, AcceptPlayerActivity::class.java))
            }

            finish()
        }
    }
}
