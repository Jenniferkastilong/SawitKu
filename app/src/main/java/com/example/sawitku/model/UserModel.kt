package com.example.sawitku.model

data class UserModel(
    var uid: String = "",
    val name: String = "",     // Pastikan field di firestore: "name" atau "nama"
    val email: String = "",
    val role: String = "",
    val aktif: Boolean = true  // Field penting untuk fitur banned
)