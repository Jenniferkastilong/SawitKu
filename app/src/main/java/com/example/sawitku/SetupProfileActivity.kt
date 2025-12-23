package com.example.sawitku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sawitku.ui.profile.SetupProfileFragment

class SetupProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_profile)

        // 1. Tangkap sinyal "is_edit_mode" dari Intent (dikirim dari Setting.kt)
        val isEditMode = intent.getBooleanExtra("is_edit_mode", false)

        if (savedInstanceState == null) {
            // 2. Buat instance Fragment
            val fragment = SetupProfileFragment()

            // 3. Bungkus data ke dalam Bundle (Argument)
            val bundle = Bundle()
            bundle.putBoolean("IS_EDIT_MODE", isEditMode) // Kita pakai key "IS_EDIT_MODE"
            fragment.arguments = bundle

            // 4. Pasang Fragment dengan data yang sudah dibungkus
            supportFragmentManager.beginTransaction()
                .replace(R.id.setup_container, fragment)
                .commit()
        }
    }
}