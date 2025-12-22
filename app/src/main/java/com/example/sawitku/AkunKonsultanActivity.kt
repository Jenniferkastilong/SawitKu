package com.example.sawitku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AkunKonsultanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ProfilKonsultanFragment())
            .commit()
    }
}
