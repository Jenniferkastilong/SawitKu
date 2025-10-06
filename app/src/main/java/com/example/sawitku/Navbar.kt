package com.example.sawitku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.petanisawit.Komunitas
import com.example.petanisawit.LaporanLahan

class Navbar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navbar, container, false)

        val btnHome = view.findViewById<Button>(R.id.btnHome)
        val btnLahan = view.findViewById<Button>(R.id.btnLahan)
        val btnKomunitas = view.findViewById<Button>(R.id.btnKomunitas)
        val btnSetting = view.findViewById<Button>(R.id.btnSetting)

        val buttons = listOf(btnHome, btnLahan, btnKomunitas, btnSetting)

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

        btnLahan.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LaporanLahan())
                .commit()
            setActiveButton(btnLahan)
        }

        btnKomunitas.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Komunitas())
                .commit()
            setActiveButton(btnKomunitas)
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