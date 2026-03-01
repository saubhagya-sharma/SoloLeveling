package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AcceptPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_player)

        val acceptButton: Button = findViewById(R.id.button_accept)
        val declineButton: Button = findViewById(R.id.button_decline)

        acceptButton.setOnClickListener {
            startActivity(Intent(this, NameEntryActivity::class.java))
        }

        declineButton.setOnClickListener {
            finishAffinity()
        }
    }
}
