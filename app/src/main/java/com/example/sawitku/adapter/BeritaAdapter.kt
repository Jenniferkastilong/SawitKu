package com.example.sawitku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.example.sawitku.model.Berita

class BeritaAdapter(private val list: List<Berita>) : RecyclerView.Adapter<BeritaAdapter.BeritaVH>() {

    inner class BeritaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val judul: TextView = itemView.findViewById(R.id.tv_judul)
        val deskripsi: TextView = itemView.findViewById(R.id.tv_deskripsi)
        val tanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_berita, parent, false)
        return BeritaVH(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BeritaVH, position: Int) {
        val item = list[position]
        holder.judul.text = item.judul
        holder.deskripsi.text = item.deskripsi
        holder.tanggal.text = item.tanggal
    }
}