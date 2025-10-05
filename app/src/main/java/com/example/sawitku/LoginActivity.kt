package com.example.sawitku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val editTextEmail: EditText = findViewById(R.id.edit_text_email_login)
        val editTextPass: EditText = findViewById(R.id.edit_text_password_login)
        val buttonLogin: Button = findViewById(R.id.button_submit_login)

        buttonLogin.setOnClickListener {
            // 🚀 Langsung pindah ke HomeActivity tanpa login logic
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

            /*
            // =============== LOGIC ASLI LOGIN (DICOMMENT SEMENTARA) ===============
            val email = editTextEmail.text.toString().trim()
            val password = editTextPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { document ->
                                    if (document != null && document.exists()) {
                                        val role = document.getString("role")
                                        Toast.makeText(this, "Login berhasil sebagai $role", Toast.LENGTH_SHORT).show()

                                        // ✅ Setelah login berhasil arahkan ke HomeActivity
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.putExtra("ROLE", role) // opsional: passing role
                                        startActivity(intent)
                                        finish()

                                        /*
                                        if (role == "Petani") {
                                            // TODO: Arahkan ke Dashboard Petani
                                        } else if (role == "Konsultan") {
                                            // TODO: Arahkan ke Dashboard Konsultan
                                        }
                                        */
                                    } else {
                                        Toast.makeText(this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal mengambil data peran.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            */
        }
    }
}