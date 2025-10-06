package com.example.petanisawit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sawitku.R

class LaporanLahan : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_laporan_lahan.xml
        val view = inflater.inflate(R.layout.fragment_laporan_lahan, container, false)

        // Contoh akses elemen UI
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Pelaporan Lahan aktif"

        // Tambahkan listener atau logika lain di sini
        // misal: tombol submit, RecyclerView, dll.

        return view
    }
}