package com.example.sawitku.ui.kalkulator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.sawitku.Navbar
import com.example.sawitku.R
import com.example.sawitku.fragment.KalkulatorFragment

class KalkulatorActivity : AppCompatActivity() {
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_kalkulator)
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.apply {
//            title = "Kalkulator Produksi"
//            setDisplayHomeAsUpEnabled(true)
//        }
//        toolbar.setNavigationOnClickListener { finish() }
//
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, KalkulatorFragment())
//                .commit()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalkulator)
//        supportActionBar?.title = "Kalkulator Hasil"
    }
}