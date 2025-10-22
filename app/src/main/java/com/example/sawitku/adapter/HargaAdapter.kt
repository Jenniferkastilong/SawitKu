package com.example.sawitku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.example.sawitku.model.Harga

class HargaAdapter(private val list: List<Harga>) : RecyclerView.Adapter<HargaAdapter.HargaVH>() {

    inner class HargaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val komoditas: TextView = itemView.findViewById(R.id.tv_komoditas)
        val harga: TextView = itemView.findViewById(R.id.tv_harga)
        val satuan: TextView = itemView.findViewById(R.id.tv_satuan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HargaVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_harga, parent, false)
        return HargaVH(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HargaVH, position: Int) {
        val item = list[position]
        holder.komoditas.text = item.komoditas
        holder.harga.text = "${item.harga}"
        holder.satuan.text = item.satuan
    }
}