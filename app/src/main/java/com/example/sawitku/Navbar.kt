package com.example.sawitku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class Navbar : Fragment() {

    private lateinit var btnHome: LinearLayout
    private lateinit var btnLahan: LinearLayout
    private lateinit var btnKomunitas: LinearLayout
    private lateinit var navbarButtons: List<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_navbar, container, false)

        btnHome = view.findViewById(R.id.btnHome)
        btnLahan = view.findViewById(R.id.btnLahan)
        btnKomunitas = view.findViewById(R.id.btnKomunitas)

        navbarButtons = listOf(btnHome, btnLahan, btnKomunitas)

<<<<<<< HEAD
=======
        fun setActiveButton(activeButton: Button) {
            buttons.forEach { it.isSelected = (it == activeButton) }
        }

        // default fragment
>>>>>>> 39971d1 (Menambahkan fitur komunitas & detail post)
        if (savedInstanceState == null) {
            loadFragment(Home())
            updateNavbarStatus(btnHome)
        }

        btnHome.setOnClickListener {
            loadFragment(Home())
            updateNavbarStatus(btnHome)
        }

        btnLahan.setOnClickListener {
            loadFragment(LaporanLahan())
            updateNavbarStatus(btnLahan)
        }

        btnKomunitas.setOnClickListener {
            loadFragment(Komunitas())
            updateNavbarStatus(btnKomunitas)
        }

        return view
    }
<<<<<<< HEAD

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateNavbarStatus(selectedMenu: LinearLayout) {
        navbarButtons.forEach { it.isSelected = (it == selectedMenu) }
    }
}
=======
}
>>>>>>> 39971d1 (Menambahkan fitur komunitas & detail post)
