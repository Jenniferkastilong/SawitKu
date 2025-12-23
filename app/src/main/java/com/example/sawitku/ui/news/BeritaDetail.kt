package com.example.sawitku

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.R

class BeritaDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_berita)

        val webView = findViewById<WebView>(R.id.webView)
        val url = intent.getStringExtra("url")

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        if (url != null) {
            webView.loadUrl(url)
        }
    }
}