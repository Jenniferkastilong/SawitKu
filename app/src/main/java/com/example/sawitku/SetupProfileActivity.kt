package com.example.sawitku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.ui.profile.SetupProfileFragment

class SetupProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.setup_container, SetupProfileFragment())
                .commit()
        }
    }
}