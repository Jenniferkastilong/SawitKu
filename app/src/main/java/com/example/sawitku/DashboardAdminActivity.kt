package com.example.sawitku

import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        tabLayout = findViewById(R.id.tabLayoutAdmin)
        viewPager = findViewById(R.id.viewPagerAdmin)

        viewPager.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "Laporan Lahan"
                1 -> "Kelola Akun"
                else -> ""
            }
        }.attach()
    }

    inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> FragmentLaporanAdmin()
            1 -> AdminAkunFragment()
            else -> Fragment()
        }
    }

    // ==================== FRAGMENT LAPORAN ADMIN ====================
    class FragmentLaporanAdmin : Fragment() {

        private val laporanAdminList = SharedData.laporanAdmin

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View {
            val v = inflater.inflate(R.layout.fragment_admin_laporan, container, false)
            val rv = v.findViewById<RecyclerView>(R.id.recyclerViewLaporan)
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = LaporanAdapter(laporanAdminList)
            return v
        }

        inner class LaporanAdapter(private val laporan: MutableList<SharedData.Laporan>) :
            RecyclerView.Adapter<LaporanAdapter.VH>() {

            inner class VH(view: View) : RecyclerView.ViewHolder(view) {
                val tvNamaKebun: TextView = view.findViewById(R.id.tv_judul)
                val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
                val tvLuas: TextView = view.findViewById(R.id.tv_luas)
                val tvLokasi: TextView = view.findViewById(R.id.tv_lokasi)
                val ivFoto: ImageView = view.findViewById(R.id.iv_foto_kebun)
                val btnDetail: Button = view.findViewById(R.id.btnDetail)
                val btnDownloadPDF: Button = view.findViewById(R.id.btnPdf)
                val btnDownloadCSV: Button = view.findViewById(R.id.btnCsv)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan_card, parent, false)
                return VH(v)
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val l = laporan[position]
                holder.tvNamaKebun.text = l.namaKebun
                holder.tvTanggal.text = l.tanggal
                holder.tvLuas.text = "Luas: ${l.luas} Ha"
                holder.tvLokasi.text = "Lokasi: ${l.lokasi.alamat}"

                // Foto
                if (!l.fotoPath.isNullOrBlank()) {
                    holder.ivFoto.setImageURI(Uri.fromFile(File(l.fotoPath)))
                } else {
                    holder.ivFoto.setImageResource(R.drawable.ic_foto_default)
                }

                // Detail → AlertDialog
                holder.btnDetail.setOnClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Detail Kebun: ${l.namaKebun}")
                    builder.setMessage(
                        """
                    Petani: ${l.namaPetani}
                    Tanggal: ${l.tanggal}
                    Luas: ${l.luas} Ha
                    Lokasi: ${l.lokasi.alamat}
                    Status: ${l.status}
                    """.trimIndent()
                    )
                    builder.setPositiveButton("Tutup", null)
                    builder.show()
                }

                holder.btnDownloadCSV.setOnClickListener { downloadCSV(l) }
                holder.btnDownloadPDF.setOnClickListener { downloadPDF(l) }
            }

            override fun getItemCount() = laporan.size

            private fun downloadCSV(laporan: SharedData.Laporan) {
                val fotoPath = laporan.fotoPath ?: ""
                val csvText = buildString {
                    appendLine("Nama Kebun,${laporan.namaKebun}")
                    appendLine("Tanggal,${laporan.tanggal}")
                    appendLine("Luas,${laporan.luas}")
                    appendLine("Lokasi,${laporan.lokasi.alamat}")
                    appendLine("Status,${laporan.status}")
                    appendLine("Foto,${fotoPath}")
                }

                val fileName = "${laporan.namaKebun}_${System.currentTimeMillis()}.csv"
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloads, fileName)
                file.writeText(csvText)
                Toast.makeText(requireContext(), "CSV tersimpan di folder Download", Toast.LENGTH_LONG).show()
            }

            private fun downloadPDF(laporan: SharedData.Laporan) {
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val pdfFile = File(downloads, "${laporan.namaKebun}_${System.currentTimeMillis()}.pdf")
                val document = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                val page = document.startPage(pageInfo)
                val canvas = page.canvas
                val paint = android.graphics.Paint().apply { textSize = 16f }

                var y = 40f
                canvas.drawText("Nama Kebun: ${laporan.namaKebun}", 20f, y, paint)
                y += 30f
                canvas.drawText("Tanggal: ${laporan.tanggal}", 20f, y, paint)
                y += 30f
                canvas.drawText("Luas: ${laporan.luas} Ha", 20f, y, paint)
                y += 30f
                canvas.drawText("Lokasi: ${laporan.lokasi.alamat}", 20f, y, paint)
                y += 30f
                canvas.drawText("Status: ${laporan.status}", 20f, y, paint)
                y += 40f

                // Tambahkan foto jika adax
                laporan.fotoPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val bitmap = android.graphics.BitmapFactory.decodeFile(path)
                        val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, 300, 200, true)
                        canvas.drawBitmap(scaledBitmap, 20f, y, paint)
                    }
                }

                document.finishPage(page)
                document.writeTo(FileOutputStream(pdfFile))
                document.close()
                Toast.makeText(requireContext(), "PDF tersimpan di folder Download", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ==================== FRAGMENT KELOLA AKUN ====================
    class AdminAkunFragment : Fragment() {

        private val db = FirebaseFirestore.getInstance()
        private val usersList = mutableListOf<SharedData.User>()
        private lateinit var adapter: UserAdapter

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            val v = inflater.inflate(R.layout.fragment_admin_akun, container, false)
            val rv = v.findViewById<RecyclerView>(R.id.recyclerViewAkun)
            rv.layoutManager = LinearLayoutManager(requireContext())
            adapter = UserAdapter(usersList)
            rv.adapter = adapter

            fetchUsersFromFirebase()

            return v
        }

        private fun fetchUsersFromFirebase() {
            db.collection("users").get().addOnSuccessListener { docs ->
                usersList.clear()
                for (doc in docs) {
                    val user = SharedData.User(
                        uid = doc.id,
                        nama = doc.getString("nama")?.takeIf { it.isNotBlank() } ?: doc.getString("email") ?: "User",
                        username = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: "",
                        aktif = doc.getBoolean("aktif") ?: true
                    )
                    usersList.add(user)
                }
                adapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal ambil user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        inner class UserAdapter(private val users: MutableList<SharedData.User>) :
            RecyclerView.Adapter<UserAdapter.VH>() {

            inner class VH(view: View) : RecyclerView.ViewHolder(view) {
                val tvNama: TextView = view.findViewById(R.id.tvNamaUser)
                val tvRole: TextView = view.findViewById(R.id.tvRoleUser)
                val btnToggle: Button = view.findViewById(R.id.btnToggleAkun)
                val btnHapus: Button = view.findViewById(R.id.btnHapusUser)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user_admin, parent, false)
                return VH(v)
            }

            override fun onBindViewHolder(holder: VH, pos: Int) {
                val u = users[pos]
                holder.tvNama.text = u.nama
                holder.tvRole.text = "Peran: ${u.role}"
                holder.btnToggle.text = if (u.aktif) "Nonaktifkan" else "Aktifkan"

                holder.btnToggle.setOnClickListener {
                    u.aktif = !u.aktif
                    notifyItemChanged(pos)
                    Toast.makeText(requireContext(), "${u.nama} ${if (u.aktif) "diaktifkan" else "dinonaktifkan"}", Toast.LENGTH_SHORT).show()
                }

                holder.btnHapus.setOnClickListener {
                    users.removeAt(pos)
                    notifyItemRemoved(pos)
                    Toast.makeText(requireContext(), "${u.nama} dihapus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun getItemCount() = users.size
        }
    }
}