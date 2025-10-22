package com.example.sawitku.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.example.sawitku.adapter.BeritaAdapter
import com.example.sawitku.model.Berita

class BeritaFragment : Fragment() {

//    private val listBerita = listOf(
//        Berita("Harga CPO Naik", "Harga minyak sawit mentah naik 5% bulan ini.", "16-10-2025"),
//        Berita("Penyakit Kelapa Sawit", "Tips mencegah penyakit pada kelapa sawit.", "15-10-2025")
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val v = inflater.inflate(R.layout.fragment_berita, container, false)
//        val rv = v.findViewById<RecyclerView>(R.id.rv_berita)
//        rv.layoutManager = LinearLayoutManager(requireContext())
//        rv.adapter = BeritaAdapter(listBerita)
//        return v
//    }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_berita, container, false)
        }

}