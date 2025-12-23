package com.example.sawitku

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
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
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Login gagal: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Ambil data user dari Firestore
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (!doc.exists()) {
                                Toast.makeText(this, "Data user tidak ditemukan di database", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            // 1. Ambil data Role, Status Aktif, dan REGION (Wilayah)
                            // Pastikan di database fieldnya bernama 'region' (atau 'wilayah', sesuaikan string-nya)
                            val role = doc.getString("role")?.lowercase() ?: ""
                            val aktif = doc.getBoolean("aktif") ?: true
                            val region = doc.getString("region") ?: "" // Default kosong jika tidak ada

                            // 2. Cek apakah akun dinonaktifkan (Fitur Hapus/Ban User)
                            if (!aktif) {
                                Toast.makeText(
                                    this,
                                    "Akun Anda dinonaktifkan. Hubungi admin.",
                                    Toast.LENGTH_LONG
                                ).show()
                                auth.signOut() // Logout paksa
                                return@addOnSuccessListener
                            }

                            // 3. SIMPAN WILAYAH KE SHAREDPREFERENCES (PENTING UNTUK FITUR 1)
                            // Ini agar di halaman Admin/Pengurus nanti kita bisa filter data
                            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("user_region", region)
                                putString("user_role", role)
                                putString("user_uid", uid)
                                apply()
                            }

                            // 4. Navigasi sesuai Role
                            // Cek khusus role petani
                            if (role == "petani") {
                                val profileComplete = doc.getBoolean("profileComplete") ?: false
                                if (!profileComplete) {
                                    // Petani pertama kali login → setup profil
                                    startActivity(Intent(this, SetupProfileActivity::class.java))
                                    finish()
                                } else {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                                return@addOnSuccessListener
                            }

                            // Role pengurus atau admin langsung ke dashboard
                            when (role) {
                                "pengurus" -> {
                                    Toast.makeText(this, "Login Pengurus Wilayah: $region", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, DashboardKonsultanActivity::class.java))
                                }
                                "admin" -> {
                                    startActivity(Intent(this, DashboardAdminActivity::class.java))
                                }
                                else -> Toast.makeText(this, "Role tidak dikenali.", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Gagal ambil data: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
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