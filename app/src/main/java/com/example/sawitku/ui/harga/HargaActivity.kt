package com.example.sawitku.ui.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.R
import com.example.sawitku.fragment.harga.HargaTbsFragment

class HargaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_harga)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_harga, HargaTbsFragment())
            .commit()
    }
}