package com.example.sawitku

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardKonsultanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetaniAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedData.initDummyData()

        // Root ScrollView + LinearLayout untuk menghindari status bar
        val scrollView = ScrollView(this)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, getStatusBarHeight() + 24, 24, 24) // kasih padding atas sesuai status bar
            setBackgroundColor(Color.parseColor("#F0F4F0"))
        }

        val title = TextView(this).apply {
            text = "Dashboard Konsultan"
            textSize = 24f
            setTextColor(Color.parseColor("#89A86C"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }
        rootLayout.addView(title)

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@DashboardKonsultanActivity)
        }
        rootLayout.addView(
            recyclerView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        scrollView.addView(rootLayout)
        setContentView(scrollView)

        adapter = PetaniAdapter(SharedData.laporanKonsultan)
        recyclerView.adapter = adapter
    }

    // Fungsi untuk mengetahui tinggi status bar
    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    inner class PetaniAdapter(private val laporanList: MutableList<SharedData.Laporan>) :
        RecyclerView.Adapter<PetaniAdapter.PetaniViewHolder>() {

        inner class PetaniViewHolder(val card: CardView) : RecyclerView.ViewHolder(card) {
            val tvNama = TextView(this@DashboardKonsultanActivity)
            val tvDetail = TextView(this@DashboardKonsultanActivity)
            val btnVerifikasi = Button(this@DashboardKonsultanActivity)
            val btnRevisi = Button(this@DashboardKonsultanActivity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetaniViewHolder {
            val card = CardView(parent.context).apply {
                radius = 20f
                setCardBackgroundColor(Color.WHITE)
                cardElevation = 8f
                useCompatPadding = true
                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 16)
                layoutParams = params
            }
            val holder = PetaniViewHolder(card)

            val layout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
            }

            holder.tvNama.textSize = 18f
            holder.tvNama.setTextColor(Color.parseColor("#1B5E20"))
            holder.tvNama.setPadding(0, 0, 0, 8)

            holder.tvDetail.textSize = 14f
            holder.tvDetail.setTextColor(Color.DKGRAY)
            holder.tvDetail.setPadding(0, 0, 0, 16)

            val btnLayout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }

            // Tombol Verifikasi
            val verifikasiBg = GradientDrawable().apply {
                setColor(Color.parseColor("#D6E7C6"))
                cornerRadius = 12f * resources.displayMetrics.density
            }
            holder.btnVerifikasi.background = verifikasiBg
            holder.btnVerifikasi.setTextColor(Color.BLACK)
            holder.btnVerifikasi.setPadding(32, 8, 32, 8)
            holder.btnVerifikasi.text = "Verifikasi"

            // Tombol Revisi
            val revisiBg = GradientDrawable().apply {
                setColor(Color.parseColor("#FF9800"))
                cornerRadius = 12f * resources.displayMetrics.density
            }
            holder.btnRevisi.background = revisiBg
            holder.btnRevisi.setTextColor(Color.WHITE)
            holder.btnRevisi.setPadding(32, 8, 32, 8)
            holder.btnRevisi.text = "Revisi"

            btnLayout.addView(holder.btnVerifikasi)
            btnLayout.addView(Space(parent.context).apply { layoutParams = LinearLayout.LayoutParams(24, 0) })
            btnLayout.addView(holder.btnRevisi)

            layout.addView(holder.tvNama)
            layout.addView(holder.tvDetail)
            layout.addView(btnLayout)

            card.addView(layout)
            return holder
        }

        override fun onBindViewHolder(holder: PetaniViewHolder, position: Int) {
            val laporan = laporanList[position]
            holder.tvNama.text = "${laporan.namaPetani} - ${laporan.namaKebun}"
            holder.tvDetail.text =
                "Luas: ${laporan.luas} Ha\nTanggal: ${laporan.tanggal}\nStatus: ${laporan.status}"

            holder.btnVerifikasi.setOnClickListener {
                laporan.verified = true
                laporan.status = "Terverifikasi"

                SharedData.laporanKonsultan.remove(laporan)
                SharedData.laporanList.add(0, laporan)
                SharedData.laporanAdmin.add(0, laporan)

                notifyItemRemoved(position)
                Toast.makeText(
                    this@DashboardKonsultanActivity,
                    "${laporan.namaPetani} berhasil diverifikasi",
                    Toast.LENGTH_SHORT
                ).show()
            }

            holder.btnRevisi.setOnClickListener {
                val catatan = "Mohon perbaiki luas kebun dan foto terbaru"
                laporan.status = "Revisi diminta"
                laporan.catatanRevisi = catatan

                Toast.makeText(
                    this@DashboardKonsultanActivity,
                    "Revisi diminta untuk ${laporan.namaPetani}",
                    Toast.LENGTH_SHORT
                ).show()
                notifyItemChanged(position)
            }
        }

        override fun getItemCount(): Int = laporanList.size
    }
}