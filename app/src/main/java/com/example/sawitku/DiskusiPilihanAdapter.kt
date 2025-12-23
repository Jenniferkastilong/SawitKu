package com.example.sawitku.ui.komunitas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R

class DiskusiPilihanAdapter : RecyclerView.Adapter<DiskusiPilihanAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgDiskusi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diskusi_pilihan, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // Menggunakan warna dari colors.xml kamu sebagai pengganti gambar
        holder.img.setImageResource(android.R.color.transparent)
        holder.img.setBackgroundResource(R.color.dgreen)
    }

    override fun getItemCount(): Int = 5
}