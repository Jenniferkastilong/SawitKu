// 1. NAMA PAKET DIPERBAIKI
package com.example.sawitku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// 2. IMPORT BINDING YANG BENAR
import com.example.sawitku.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    // 3. DEKLARASI VIEW BINDING
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 4. MENGGUNAKAN VIEW BINDING UNTUK MENAMPILKAN LAYOUT
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.buttonSubmitRegister.setOnClickListener {
            // Mengambil nilai langsung dari binding, lebih aman!
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val selectedRoleId = binding.radioGroupRole.checkedRadioButtonId

            if (email.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRoleButton: RadioButton = findViewById(selectedRoleId)
            val role = selectedRoleButton.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val userMap = hashMapOf(
                                "email" to email,
                                "role" to role
                            )

                            db.collection("users").document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                    // Arahkan ke halaman login atau main activity
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }

                    } else {
                        Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}