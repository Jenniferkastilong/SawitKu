package com.example.sawitku

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LaporanAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_laporan, container, false)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewLaporan)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = LaporanAdapter(SharedData.laporanAdmin)

        return view
    }

    inner class LaporanAdapter(
        private val data: MutableList<SharedData.Laporan>
    ) : RecyclerView.Adapter<LaporanAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvNama: TextView = v.findViewById(R.id.tv_judul)
            val tvTanggal: TextView = v.findViewById(R.id.tv_tanggal)
            val tvLuas: TextView = v.findViewById(R.id.tv_luas)
            val tvLokasi: TextView = v.findViewById(R.id.tv_lokasi)
            val ivFoto: ImageView = v.findViewById(R.id.iv_foto_kebun)
            val btnDetail: Button = v.findViewById(R.id.btnDetail)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_laporan_card, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val l = data[pos]
            holder.tvNama.text = l.namaKebun
            holder.tvTanggal.text = l.tanggal
            holder.tvLuas.text = "Luas: ${l.luas} Ha"
            holder.tvLokasi.text = l.lokasi.alamat

            if (!l.fotoPath.isNullOrBlank()) {
                holder.ivFoto.setImageURI(Uri.fromFile(File(l.fotoPath)))
            }

            holder.btnDetail.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Detail Kebun")
                    .setMessage(
                        """
                        Petani: ${l.namaPetani}
                        Luas: ${l.luas} Ha
                        Status: ${l.status}
                        """.trimIndent()
                    )
                    .setPositiveButton("Tutup", null)
                    .show()
            }
        }

        override fun getItemCount() = data.size
    }
}
