package com.example.sololeveling

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sololeveling.core.GameManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GameManager.initializePlayer(name = "Zerith")
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
