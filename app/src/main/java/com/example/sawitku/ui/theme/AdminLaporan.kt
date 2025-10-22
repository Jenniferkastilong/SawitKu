package com.example.sawitku

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AdminLaporanFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_admin_laporan, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.recyclerViewLaporan)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = AdminAdapter(SharedData.laporanList.toMutableList())
        return v
    }

    // Adapter Admin langsung di sini
    inner class AdminAdapter(private val items: MutableList<SharedData.Laporan>) :
        RecyclerView.Adapter<AdminAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvInfo: TextView = v.findViewById(R.id.tv_judul)
            val tvTanggal: TextView = v.findViewById(R.id.tv_tanggal)
            val tvLuas: TextView = v.findViewById(R.id.tv_luas)
            val tvLokasi: TextView = v.findViewById(R.id.tv_lokasi)
            val ivFoto: ImageView = v.findViewById(R.id.iv_foto_kebun)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan_card, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val laporan = items[position]

            holder.tvInfo.text = "${laporan.namaKebun} · ${laporan.namaPetani}"
            holder.tvTanggal.text = laporan.tanggal
            holder.tvLuas.text = "Luas: ${laporan.luas} Ha"
            holder.tvLokasi.text = "Lokasi: ${String.format("%.4f", laporan.lokasi.latitude)}, ${String.format("%.4f", laporan.lokasi.longitude)}"

            if (!laporan.fotoPath.isNullOrBlank()) {
                holder.ivFoto.setImageURI(Uri.fromFile(File(laporan.fotoPath)))
            } else {
                holder.ivFoto.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            holder.itemView.setOnClickListener {
                Toast.makeText(requireContext(), "Status: ${laporan.status}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = items.size
    }
}