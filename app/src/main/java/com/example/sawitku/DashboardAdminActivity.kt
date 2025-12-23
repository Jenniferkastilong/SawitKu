package com.example.sawitku

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        tabLayout = findViewById(R.id.tabLayoutAdmin)
        viewPager = findViewById(R.id.viewPagerAdmin)

        viewPager.adapter = AdminPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = if (pos == 0) "Laporan Lahan" else "Kelola Akun"
        }.attach()

        bottomNav = findViewById(R.id.bottom_nav_admin)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    findViewById<View>(R.id.layout_dashboard).visibility = View.VISIBLE
                    findViewById<View>(R.id.layout_profile).visibility = View.GONE
                    true
                }

                R.id.nav_akun -> {
                    findViewById<View>(R.id.layout_dashboard).visibility = View.GONE
                    findViewById<View>(R.id.layout_profile).visibility = View.VISIBLE

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_profile, ProfilAdminFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }

    /* ================= VIEWPAGER ================= */

    private inner class AdminPagerAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            if (position == 0) LaporanAdminFragment() else AdminAkunFragment()
    }

    /* ================= LAPORAN ================= */

    class LaporanAdminFragment : Fragment() {

        override fun onCreateView(
            inflater: android.view.LayoutInflater,
            container: android.view.ViewGroup?,
            savedInstanceState: Bundle?
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

            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
                val v = android.view.LayoutInflater.from(parent.context)
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
                } else {
                    holder.ivFoto.setImageResource(R.drawable.ic_foto_default)
                }

                holder.btnDetail.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Detail Kebun")
                        .setMessage(
                            "Petani: ${l.namaPetani}\n" +
                                    "Luas: ${l.luas} Ha\n" +
                                    "Status: ${l.status}"
                        )
                        .setPositiveButton("Tutup", null)
                        .show()
                }
            }

            override fun getItemCount(): Int = data.size
        }
    }
}
