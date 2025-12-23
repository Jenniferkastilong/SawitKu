package com.example.sawitku

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Jika belum ada Glide, lihat catatan di bawah
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminLaporanFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: AdminAdapter
    private val listLaporan = ArrayList<LaporanModel>() // Menggunakan Model Khusus
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_admin_laporan, container, false)

        db = FirebaseFirestore.getInstance()

        rv = v.findViewById(R.id.recyclerViewLaporan)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dengan list kosong dulu
        adapter = AdminAdapter(listLaporan)
        rv.adapter = adapter

        // Panggil fungsi untuk ambil data dari Firebase
        getDataLaporan()

        return v
    }

    private fun getDataLaporan() {
        // 1. Ambil Data Role & Region dari SharedPreferences (Login)
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "")
        val userRegion = sharedPref.getString("user_region", "")

        val collRef = db.collection("laporan_lahan")

        // 2. Logic Filter: Admin lihat semua, Pengurus lihat region-nya saja
        val query: Query = if (userRole == "pengurus") {
            collRef.whereEqualTo("region", userRegion)
        } else {
            collRef // Admin / Super Admin ambil semua
        }

        // 3. Realtime Listener
        query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                listLaporan.clear()
                for (doc in snapshot.documents) {
                    // Mapping manual agar aman (atau pakai toObject jika nama field 100% sama)
                    val data = LaporanModel(
                        id = doc.id,
                        namaKebun = doc.getString("namaKebun") ?: "-",
                        namaPetani = doc.getString("namaPetani") ?: "-",
                        tanggal = doc.getString("tanggal") ?: "-",
                        luas = doc.getString("luas") ?: "0",
                        lokasiStr = doc.getString("lokasiString") ?: "Lokasi tidak ada", // Simpan lat,long sebagai string di db
                        fotoUrl = doc.getString("fotoPath") ?: "", // URL Foto dari Firebase Storage
                        region = doc.getString("region") ?: ""
                    )
                    listLaporan.add(data)
                }
                adapter.notifyDataSetChanged()

                if (listLaporan.isEmpty()) {
                    Toast.makeText(context, "Tidak ada data untuk wilayah $userRegion", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- CLASS MODEL DATA SEDERHANA UNTUK FIRESTORE ---
    data class LaporanModel(
        val id: String,
        val namaKebun: String,
        val namaPetani: String,
        val tanggal: String,
        val luas: String,
        val lokasiStr: String,
        val fotoUrl: String,
        val region: String
    )

    // --- ADAPTER ---
    inner class AdminAdapter(private val items: MutableList<LaporanModel>) :
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
            holder.tvLokasi.text = laporan.lokasiStr

            // LOGIC GAMBAR
            // Jika kamu belum pakai Glide, ganti baris ini dengan ivFoto.setImageResource(...) sementara
            if (laporan.fotoUrl.isNotEmpty()) {
                // Disarankan pakai Library Glide: implementation 'com.github.bumptech.glide:glide:4.12.0'
                // Jika tidak ada Glide, kode ini akan merah. Hapus jika error dan pakai placeholder.
                try {
                    Glide.with(requireContext()).load(laporan.fotoUrl).into(holder.ivFoto)
                } catch (e: Exception) {
                    holder.ivFoto.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            } else {
                holder.ivFoto.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            // Klik Biasa -> Detail (Bisa ditambahkan intent ke detail activity)
            holder.itemView.setOnClickListener {
                Toast.makeText(requireContext(), "Region: ${laporan.region}", Toast.LENGTH_SHORT).show()
            }

            // Klik Tahan (Long Click) -> Fitur Hapus Laporan
            holder.itemView.setOnLongClickListener {
                showDeleteDialog(laporan)
                true
            }
        }

        private fun showDeleteDialog(laporan: LaporanModel) {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Laporan?")
                .setMessage("Data laporan kebun ${laporan.namaKebun} akan dihapus permanen.")
                .setPositiveButton("Hapus") { _, _ ->
                    db.collection("laporan_lahan").document(laporan.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Laporan berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        override fun getItemCount(): Int = items.size
    }
}