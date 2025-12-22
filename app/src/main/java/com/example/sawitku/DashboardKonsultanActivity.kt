package com.example.sawitku

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardKonsultanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetaniAdapter
    private lateinit var rootLayout: RelativeLayout
    private lateinit var navbar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedData.initDummyData()

        rootLayout = RelativeLayout(this)

        val scrollView = ScrollView(this)
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, getStatusBarHeight() + 24, 24, 24)
            setBackgroundColor(Color.parseColor("#F0F4F0"))
        }

        val title = TextView(this).apply {
            text = "Dashboard Konsultan"
            textSize = 24f
            setTextColor(Color.parseColor("#89A86C"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@DashboardKonsultanActivity)
        }

        contentLayout.addView(title)
        contentLayout.addView(
            recyclerView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        scrollView.addView(contentLayout)

        val contentParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        rootLayout.addView(scrollView, contentParams)

        navbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.WHITE)
            elevation = 16f
            id = View.generateViewId()
        }

        val btnHome = navButton("Beranda")
        val btnAkun = navButton("Akun")
        navbar.addView(btnHome)
        navbar.addView(btnAkun)

        val navParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }

        rootLayout.addView(navbar, navParams)
        setContentView(rootLayout)

        btnHome.setOnClickListener {
            showBeranda()
        }

        btnAkun.setOnClickListener {
            showProfilKonsultan()
        }

        adapter = PetaniAdapter(SharedData.laporanKonsultan)
        recyclerView.adapter = adapter
    }

    private fun navButton(text: String): Button =
        Button(this).apply {
            this.text = text
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(0, 140, 1f)
        }

    private fun getStatusBarHeight(): Int {
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) resources.getDimensionPixelSize(resId) else 0
    }

    /* ================= BERANDA ================= */
    private fun showBeranda() {
        val scrollView = ScrollView(this)
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, getStatusBarHeight() + 24, 24, 24)
            setBackgroundColor(Color.parseColor("#F0F4F0"))
        }

        val title = TextView(this).apply {
            text = "Dashboard Konsultan"
            textSize = 24f
            setTextColor(Color.parseColor("#89A86C"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@DashboardKonsultanActivity)
            adapter = PetaniAdapter(SharedData.laporanKonsultan)
        }

        contentLayout.addView(title)
        contentLayout.addView(recyclerView)
        scrollView.addView(contentLayout)

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        ).apply { addRule(RelativeLayout.ABOVE, navbar.id) }

        rootLayout.removeAllViews()
        rootLayout.addView(scrollView, params)
        rootLayout.addView(navbar, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply { addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) })
    }

    /* ================= PROFIL KONSULTAN ================= */
    private fun showProfilKonsultan() {
        val scrollView = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, getStatusBarHeight() + 24, 24, 24)
            setBackgroundColor(Color.parseColor("#F0F4F0"))
        }

        val title = TextView(this).apply {
            text = "Profil Konsultan"
            textSize = 24f
            setTextColor(Color.parseColor("#89A86C"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)
            elevation = 8f
        }

        val ivProfile = ImageView(this).apply {
            setImageResource(R.drawable.ic_profile)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER
            }
        }

        val tvNama = TextView(this).apply {
            text = "Konsultan"
            textSize = 18f
            setTextColor(Color.parseColor("#1B5E20"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 4)
        }

        val tvRole = TextView(this).apply {
            text = "Konsultan Utama"
            textSize = 16f
            setTextColor(Color.parseColor("#4CAF50"))
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 12)
        }

        val btnEdit = Button(this).apply {
            text = "Edit Profil"
            setBackgroundColor(Color.parseColor("#C8E6C9"))
            setTextColor(Color.BLACK)
        }

        val btnLogout = Button(this).apply {
            text = "Keluar"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
        }

        btnEdit.setOnClickListener {
            startActivity(Intent(this, SetupProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            finish()
        }

        card.addView(ivProfile)
        card.addView(tvNama)
        card.addView(tvRole)
        card.addView(btnEdit)
        card.addView(btnLogout)

        layout.addView(title)
        layout.addView(card)

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        ).apply { addRule(RelativeLayout.ABOVE, navbar.id) }

        rootLayout.removeAllViews()
        rootLayout.addView(scrollView, params)
        rootLayout.addView(navbar, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply { addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) })

        scrollView.addView(layout)
    }

    /* ================= ADAPTER PETANI ================= */
    inner class PetaniAdapter(private val laporanList: MutableList<SharedData.Laporan>) :
        RecyclerView.Adapter<PetaniAdapter.VH>() {

        inner class VH(val card: CardView) : RecyclerView.ViewHolder(card) {
            val tvNama = TextView(this@DashboardKonsultanActivity)
            val tvDetail = TextView(this@DashboardKonsultanActivity)
            val btnVerifikasi = Button(this@DashboardKonsultanActivity)
            val btnRevisi = Button(this@DashboardKonsultanActivity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val card = CardView(parent.context).apply {
                radius = 20f
                setCardBackgroundColor(Color.WHITE)
                cardElevation = 8f
                useCompatPadding = true
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 16, 0, 16) }
            }

            val holder = VH(card)

            val layout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
            }

            holder.tvNama.apply { textSize = 18f; setTextColor(Color.parseColor("#1B5E20")) }
            holder.tvDetail.apply { textSize = 14f; setTextColor(Color.DKGRAY) }

            val btnLayout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }

            holder.btnVerifikasi.apply {
                text = "VERIFIKASI"
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#D6E7C6"))
                    cornerRadius = 24f
                }
                setTextColor(Color.BLACK)
            }

            holder.btnRevisi.apply {
                text = "REVISI"
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#FF9800"))
                    cornerRadius = 24f
                }
                setTextColor(Color.WHITE)
            }

            btnLayout.addView(holder.btnVerifikasi)
            btnLayout.addView(Space(parent.context).apply { layoutParams = LinearLayout.LayoutParams(24, 0) })
            btnLayout.addView(holder.btnRevisi)

            layout.addView(holder.tvNama)
            layout.addView(holder.tvDetail)
            layout.addView(btnLayout)
            card.addView(layout)

            return holder
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val laporan = laporanList[position]
            holder.tvNama.text = "${laporan.namaPetani} - ${laporan.namaKebun}"
            holder.tvDetail.text = "Luas: ${laporan.luas} Ha\nTanggal: ${laporan.tanggal}\nStatus: ${laporan.status}"

            holder.btnVerifikasi.setOnClickListener {
                Toast.makeText(this@DashboardKonsultanActivity, "Berhasil diverifikasi", Toast.LENGTH_SHORT).show()
            }

            holder.btnRevisi.setOnClickListener {
                Toast.makeText(this@DashboardKonsultanActivity, "Revisi diminta", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = laporanList.size
    }
}
