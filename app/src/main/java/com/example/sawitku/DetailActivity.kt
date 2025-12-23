package com.example.sawitku.ui.komunitas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.R

class DetailPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)

        // Ambil data dari Intent
        val title = intent.getStringExtra("EXTRA_TITLE")
        val content = intent.getStringExtra("EXTRA_CONTENT")
        val author = intent.getStringExtra("EXTRA_AUTHOR")

        // Pasang ke View
        findViewById<TextView>(R.id.tvDetailTitle).text = title
        findViewById<TextView>(R.id.tvDetailContent).text = content
        findViewById<TextView>(R.id.tvDetailAuthor).text = "Oleh: $author"

        // Tombol kembali
        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}