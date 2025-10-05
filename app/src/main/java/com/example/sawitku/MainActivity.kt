package com.example.sawitku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.HomeActivity
import com.example.sawitku.LoginActivity
import com.example.sawitku.R
import com.example.sawitku.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // User sudah login → langsung ke HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // Tombol Login & Register
        val btnLogin: Button = findViewById(R.id.button_login_main)
        val btnRegister: Button = findViewById(R.id.button_register_main)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}