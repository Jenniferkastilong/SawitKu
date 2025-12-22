package com.example.sawitku

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HargaActivity : AppCompatActivity() {

    private lateinit var bigChart: LineChart
    private lateinit var spinnerWilayah: Spinner
    private lateinit var tvPrediction: TextView
    private lateinit var tvPercentage: TextView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama layout benar (activity_harga atau fragment_harga)
        setContentView(R.layout.fragment_harga)

        // Inisialisasi View
        bigChart = findViewById(R.id.bigChartHarga)
        spinnerWilayah = findViewById(R.id.spinnerWilayah)
        tvPrediction = findViewById(R.id.tvPrediction)
        tvPercentage = findViewById(R.id.tvPercentage)

        setupChartStyle()
        setupSpinner()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupChartStyle() {
        bigChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.textColor = Color.WHITE
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setDrawGridBackground(false)
        }
    }

    private fun setupSpinner() {
        val listWilayah = arrayOf("RIAU", "JAMBI", "SUMATERA UTARA", "KALIMANTAN BARAT")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listWilayah)
        spinnerWilayah.adapter = adapter

        spinnerWilayah.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadChartData(listWilayah[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadChartData(wilayah: String) {
        firestore.collection("harga_tbs")
            .whereEqualTo("wilayah", wilayah)
            .orderBy("tanggal", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                // PERBAIKAN: Gunakan Entry dari library MPAndroidChart
                val entries = ArrayList<Entry>()

                if (!snapshot.isEmpty) {
                    snapshot.documents.forEachIndexed { index, doc ->
                        val harga = doc.getLong("harga")?.toFloat() ?: 0f
                        entries.add(Entry(index.toFloat(), harga))
                    }
                    updateUI(entries)
                } else {
                    // Jika data kosong, bersihkan chart
                    bigChart.clear()
                    tvPrediction.text = "Rp 0"
                    tvPercentage.text = "0%"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Harga TBS").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.WHITE
            setCircleColor(Color.WHITE)
            lineWidth = 3f
            valueTextColor = Color.WHITE
            setDrawFilled(true)
            // Pastikan Anda sudah membuat file drawable ini sebelumnya
            fillDrawable = ContextCompat.getDrawable(this@HargaActivity, R.drawable.bg_gradient_cart)
        }

        bigChart.data = LineData(dataSet)
        bigChart.animateX(800)
        bigChart.invalidate()

        if (entries.isNotEmpty()) {
            val lastPrice = entries.last().y
            tvPrediction.text = "Rp ${String.format("%,.0f", lastPrice)}"

            if (entries.size >= 2) {
                val prevPrice = entries[entries.size - 2].y
                val diff = ((lastPrice - prevPrice) / prevPrice) * 100

                tvPercentage.text = String.format("%.1f%%", diff)
                if (diff >= 0) {
                    tvPercentage.setTextColor(Color.GREEN)
                    tvPercentage.text = "+${tvPercentage.text}"
                } else {
                    tvPercentage.setTextColor(Color.RED)
                }
            }
        }
    }
}