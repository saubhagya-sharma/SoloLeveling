package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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

    private lateinit var runeDescriptionText: TextView
    private lateinit var startBossButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val container = findViewById<LinearLayout>(R.id.container_inventory_items)

        val runeView = layoutInflater.inflate(R.layout.item_inventory_card, container, false)
        runeView.elevation = 12f

        runeView.findViewById<TextView>(R.id.text_item_name).text = "Rune Stone"
        runeDescriptionText = runeView.findViewById(R.id.text_description)
        runeDescriptionText.text = "Use rune stone to summon boss workout"

        runeView.findViewById<ImageView>(R.id.icon_item).setImageResource(R.drawable.ic_rune)

        startBossButton = runeView.findViewById(R.id.button_action)
        startBossButton.text = "Start Boss Workout"
        startBossButton.setOnClickListener {
            startBossBattle()
        }

        container.addView(runeView)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            BossManager.deleteExpiredItems(database)
            if (database.inventoryDao().getItem("RUNE_STONE") == null) {
                runeDescriptionText.text = "No rune stone available. Earn one to summon a boss workout."
                startBossButton.isEnabled = false
                startBossButton.alpha = 0.55f
                return@launch
            }

            val daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(),
                LocalDate.parse(rune.expiryDate)
            ).coerceAtLeast(0)

            runeDescriptionText.text =
                "Use rune stone to summon boss workout. Expires in $daysLeft day${if (daysLeft == 1L) "" else "s"}."
            startBossButton.isEnabled = true
            startBossButton.alpha = 1f
        }
    }

    private fun startBossBattle() {
        lifecycleScope.launch {
            if (database.inventoryDao().getItem("RUNE_STONE") == null) {
                Toast.makeText(this@InventoryActivity, "No Rune Stone available.", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            val boss = BossManager.generateBoss(database)
            if (boss == null) {
                Toast.makeText(
                    this@InventoryActivity,
                    "No eligible exercises with PR yet.",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            startActivity(Intent(this@InventoryActivity, BossBattleActivity::class.java))
            finish()
        }
    }
}
