package com.example.sawitku

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activiti_notification)

        findViewById<ImageButton>(R.id.btnBackNotif).setOnClickListener {
            finish()
        }
    }
}