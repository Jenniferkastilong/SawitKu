package com.example.sawitku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField = findViewById<EditText>(R.id.edit_text_email_login)
        val passField = findViewById<EditText>(R.id.edit_text_password_login)
        val btnLogin = findViewById<Button>(R.id.button_submit_login)
        val txtForgot = findViewById<TextView>(R.id.text_forgot_password)

        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                if (!doc.exists()) {
                                    Toast.makeText(this, "Data user hilang, silakan registrasi ulang.", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                    startActivity(Intent(this, RegisterActivity::class.java))
                                    finish()
                                    return@addOnSuccessListener
                                }

                                val role = doc.getString("role") ?: ""
                                val profileCompleted = doc.getBoolean("profileCompleted") ?: false
                                val aktif = doc.getBoolean("aktif") ?: true

                                if (!aktif) {
                                    Toast.makeText(this, "Akun Anda dinonaktifkan. Hubungi admin.", Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    return@addOnSuccessListener
                                }

                                when (role.lowercase()) {
                                    "petani" -> {
                                        if (!profileCompleted) {
                                            startActivity(Intent(this, SetupProfileActivity::class.java))
                                        } else {
                                            startActivity(Intent(this, HomeActivity::class.java))
                                        }
                                    }
                                    "pengurus" -> startActivity(Intent(this, DashboardKonsultanActivity::class.java))
                                    "admin" -> startActivity(Intent(this, DashboardAdminActivity::class.java))
                                    else -> Toast.makeText(this, "Role tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                }
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal ambil data: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        txtForgot.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email untuk reset password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengirim email reset: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}