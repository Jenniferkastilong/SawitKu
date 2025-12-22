package com.example.sawitku

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sawitku.ui.news.BeritaActivity
import com.example.sawitku.HargaActivity
import com.example.sawitku.ui.profile.Profile
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Home : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvPriceValue: TextView
    private lateinit var tvLabelWilayah: TextView
    private lateinit var ivProfile: ImageView
    private lateinit var lineChart: LineChart
    private lateinit var ivCommunity: ImageView
    private lateinit var btnNotification: FrameLayout

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Pastikan layout ini adalah fragment_home.xml yang berisi Card Harga dan Quick Button
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // Binding Views
        tvUsername = v.findViewById(R.id.tv_username)
        tvPriceValue = v.findViewById(R.id.tv_price_value)
        tvLabelWilayah = v.findViewById(R.id.tv_label_wilayah) // Sesuai ID di XML Anda
        ivProfile = v.findViewById(R.id.iv_profile_pic)
        lineChart = v.findViewById(R.id.line_chart_harga)
        ivCommunity = v.findViewById(R.id.iv_community_latest)
        btnNotification = v.findViewById(R.id.btn_notification)

        setupChartStyle()
        loadUserData()
        loadCommunityData()
        setupNavigation(v)

        return v
    }

    private fun setupChartStyle() {
        lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false) // Di Home cukup visual saja
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setDrawGridBackground(false)
            // Menghilangkan offset agar grafik memenuhi area kartu
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get().addOnSuccessListener { doc ->
            if (!isAdded) return@addOnSuccessListener

            val nama = doc.getString("nama") ?: "User"
            val wilayah = doc.getString("wilayahId") ?: "RIAU"

            tvUsername.text = nama
            tvLabelWilayah.text = "HARGA TBS $wilayah"

            Glide.with(this).load(doc.getString("foto_url"))
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(ivProfile)

            // Setelah tahu wilayahnya, ambil data harganya
            loadPriceData(wilayah)
        }
    }

    private fun loadPriceData(wilayah: String) {
        firestore.collection("harga_tbs")
            .whereEqualTo("wilayah", wilayah)
            .orderBy("tanggal", Query.Direction.ASCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded) return@addOnSuccessListener

                val entries = ArrayList<Entry>()
                var lastPrice = 0f

                if (snapshot.isEmpty) {
                    // Fallback data simulasi ML jika database kosong
                    entries.add(Entry(0f, 2200f))
                    entries.add(Entry(1f, 2350f))
                    entries.add(Entry(2f, 2300f))
                    entries.add(Entry(3f, 2450f))
                    lastPrice = 2450f
                } else {
                    snapshot.documents.forEachIndexed { index, doc ->
                        val harga = doc.getDouble("harga")?.toFloat() ?: 0f
                        entries.add(Entry(index.toFloat(), harga))
                        lastPrice = harga
                    }
                }

                tvPriceValue.text = "Rp ${String.format("%,.0f", lastPrice)} /kg"
                updateChart(entries)
            }
    }

    private fun updateChart(entries: List<Entry>) {
        if (entries.isEmpty() || !isAdded) return

        val dataSet = LineDataSet(entries, "Harga").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.WHITE
            lineWidth = 3f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradient_cart)
        }

        lineChart.data = LineData(dataSet)
        lineChart.animateY(1000)
        lineChart.invalidate()
    }

    private fun loadCommunityData() {
        firestore.collection("community")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded || snapshot.isEmpty) return@addOnSuccessListener
                val imageUrl = snapshot.documents[0].getString("imageUrl")
                Glide.with(this).load(imageUrl).into(ivCommunity)
            }
    }

    private fun setupNavigation(v: View) {
        // Navigasi ke Profil
        ivProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Profile())
                .addToBackStack(null)
                .commit()
        }

        // Navigasi ke Notifikasi (Activity)
        btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        // Quick Buttons (Navigasi ke Activity Berita & Harga)
        v.findViewById<View>(R.id.btn_nav_berita).setOnClickListener {
            startActivity(Intent(requireContext(), BeritaActivity::class.java))
        }

        v.findViewById<View>(R.id.btn_nav_harga).setOnClickListener {
            startActivity(Intent(requireContext(), HargaActivity::class.java))
        }
    }
}