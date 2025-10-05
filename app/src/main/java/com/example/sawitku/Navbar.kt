package com.example.sawitku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class Navbar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navbar, container, false)

        val btnHome = view.findViewById<Button>(R.id.btnHome)
        val btnProfile = view.findViewById<Button>(R.id.btnProfile)
        val btnSetting = view.findViewById<Button>(R.id.btnSetting)

        val buttons = listOf(btnHome, btnProfile, btnSetting)

        fun setActiveButton(activeButton: Button) {
            buttons.forEach { it.isSelected = (it == activeButton) }
        }

        // tampilkan Home fragment pertama kali
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
            setActiveButton(btnHome)
        }

        btnHome.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Home())
                .commit()
            setActiveButton(btnHome)
        }

        btnProfile.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Profile())
                .commit()
            setActiveButton(btnProfile)
        }

        btnSetting.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Setting())
                .commit()
            setActiveButton(btnSetting)
        }

        return view
    }
}