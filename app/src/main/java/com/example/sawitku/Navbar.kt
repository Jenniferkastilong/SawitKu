package com.example.sawitku

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Navbar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // load fragment pertama kali (Home)
        if (savedInstanceState == null) {
            replaceFragment(Home())
        }

        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnSetting = findViewById<Button>(R.id.btnSetting)

        btnHome.setOnClickListener {
            replaceFragment(Home())
        }

        btnProfile.setOnClickListener {
            replaceFragment(Profile())
        }

        btnSetting.setOnClickListener {
            replaceFragment(Setting())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}