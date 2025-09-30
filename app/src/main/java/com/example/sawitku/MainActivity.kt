// PERBAIKAN UTAMA ADA DI BARIS INI
package com.example.sawitku // Pastikan ini sesuai dengan struktur folder proyek Anda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
// Hapus 'import android.R' jika ada, karena itu salah.
// Import R yang benar akan otomatis ditambahkan jika nama package sudah sesuai.

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Kode di bawah ini seharusnya tidak error lagi setelah package name diperbaiki
        val buttonLogin: Button = findViewById(R.id.button_login)
        val buttonRegister: Button = findViewById(R.id.button_register)

        buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}