package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
                GameManager.initializePlayer(name)
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
