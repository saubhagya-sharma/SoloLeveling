package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.core.BossManager
import com.example.sololeveling.data.local.DatabaseProvider
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class InventoryActivity : AppCompatActivity() {

    private val database by lazy { DatabaseProvider.getDatabase(this) }

    private lateinit var runeStatusText: TextView
    private lateinit var startBossButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        runeStatusText = findViewById(R.id.text_rune_status)
        startBossButton = findViewById(R.id.button_start_boss_battle)

        startBossButton.setOnClickListener {
            lifecycleScope.launch {
                val rune = database.inventoryDao().getItem("RUNE_STONE")
                if (rune == null) {
                    Toast.makeText(this@InventoryActivity, "No Rune Stone available.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val boss = BossManager.generateBoss(database)
                if (boss == null) {
                    Toast.makeText(this@InventoryActivity, "No eligible exercises with PR yet.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                database.inventoryDao().deleteById(rune.id)

                startActivity(Intent(this@InventoryActivity, BossBattleActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            BossManager.deleteExpiredItems(database)
            val rune = database.inventoryDao().getItem("RUNE_STONE")
            if (rune == null) {
                runeStatusText.text = "Rune Stone: none"
                startBossButton.isEnabled = false
                return@launch
            }

            val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(rune.expiryDate)).coerceAtLeast(0)
            runeStatusText.text = "Rune Stone ($daysLeft days left)"
            startBossButton.isEnabled = true
        }
    }
}
