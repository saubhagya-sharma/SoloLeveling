package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.domain.PlayerProfile
import com.example.sololeveling.domain.Stat
import com.example.sololeveling.domain.StatType
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(this)

        lifecycleScope.launch {
            val playerEntity = database.playerDao().getPlayer()

            if (playerEntity != null) {

                // Rebuild PlayerProfile from DB
                val baseStats = listOf(
                    Stat(type = StatType.STRENGTH),
                    Stat(type = StatType.ENDURANCE),
                    Stat(type = StatType.STAMINA),
                    Stat(type = StatType.DISCIPLINE)
                )

                GameManager.player = PlayerProfile(
                    name = playerEntity.name,
                    stats = baseStats
                )

                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, AcceptPlayerActivity::class.java))
            }

            finish()
        }
    }
}