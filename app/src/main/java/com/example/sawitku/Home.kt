package com.example.sawitku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val ivProfile = view.findViewById<ShapeableImageView>(R.id.iv_profile_pic)

        ivProfile.setOnClickListener {
            // ganti fragment menjadi Profile di parent fragment container
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Profile())
                .commit()

            // Opsional: set tombol profil aktif di Navbar (butuh reference atau listener)
        }

        return view
    }
}