package com.example.sawitku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek user sudah login atau belum
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists() && doc.getBoolean("profileComplete") == true) {
                        // User lengkap → langsung ke Home
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // User login tapi datanya hilang / belum lengkap → logout dan kembali ke MainActivity
                        auth.signOut()
                        Toast.makeText(this, "Data user tidak ditemukan, silakan login/register lagi", Toast.LENGTH_LONG).show()
                        setContentView(R.layout.activity_main)
                        setupButtons()
                    }
                }
                .addOnFailureListener {
                    // Gagal baca Firestore → logout
                    auth.signOut()
                    Toast.makeText(this, "Gagal memeriksa data, silakan login lagi", Toast.LENGTH_LONG).show()
                    setContentView(R.layout.activity_main)
                    setupButtons()
                }
        } else {
            // User belum login → tampilkan layout normal
            setContentView(R.layout.activity_main)
            setupButtons()
        }
    }

    private fun setupButtons() {
        val btnLogin = findViewById<Button>(R.id.button_login_main)
        val btnRegister = findViewById<Button>(R.id.button_register_main)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}