// File: com/example/sawitku/model/User.kt
package com.example.sawitku.model

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null, // "admin", "petani", "konsultan"
    val region: String? = null, // Contoh: "DIY", "Jateng", "Sumut"
    val isActive: Boolean = true // Untuk fitur nonaktifkan akun
)