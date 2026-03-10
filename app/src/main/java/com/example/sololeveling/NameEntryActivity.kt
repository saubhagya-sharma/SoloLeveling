package com.example.sololeveling

import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sololeveling.core.GameManager

class NameEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        val nameEditText: EditText = findViewById(R.id.edit_text_name)
        val confirmButton: Button = findViewById(R.id.button_confirm)

        confirmButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()

            if (name.isNotBlank()) {

                val database = com.example.sololeveling.data.local.DatabaseProvider.getDatabase(this)

                lifecycleScope.launch {
                    database.playerDao().insertPlayer(
                        com.example.sololeveling.data.local.entity.PlayerEntity(
                            name = name
                        )
                    )
                    GameManager.initializePlayer(name)
                    startActivity(SetGoalActivity.createIntent(this@NameEntryActivity))
                    finish()
                }

            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
