package com.example.sawitku.fragment.harga

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.R
import com.example.sawitku.databinding.ItemTbsBinding
import com.example.sawitku.fragment.HargaFragment
import com.example.sawitku.fragment.HargaFragment.TbsAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class HargaTbsFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var chart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.harga_tbs, container, false)
        rv = v.findViewById(R.id.rvTBS)
        chart = v.findViewById(R.id.lineChart)

        rv.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            try {
                val dataRaw = fetchRSSData()
                val data = deduplicateRuntime(dataRaw)

                rv.adapter = TbsAdapter(data)
                setupChart(data)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal load data", Toast.LENGTH_SHORT).show()
            }
        }

        return v
    }

    private suspend fun fetchRSSData(): List<HargaFragment.DataTBS> = withContext(Dispatchers.IO) {
        val rssUrl = URL("https://www.infosawit.com/feed/")
        val input = rssUrl.openConnection().getInputStream()

        return@withContext listOf(
            HargaFragment.DataTBS(
                provinsi = "Riau",
                periode = "Nov 2025",
                cpoAverage = 9000.0,
                intiSawitAverage = 5000.0,
                hargaTBS = listOf(HargaFragment.HargaTBS(3, 1450.0),
                    HargaFragment.HargaTBS(4, 1550.0)
                )
            ),
            HargaFragment.DataTBS(
                provinsi = "Sumatera Utara",
                periode = "Nov 2025",
                cpoAverage = 8800.0,
                intiSawitAverage = 4800.0,
                hargaTBS = listOf(HargaFragment.HargaTBS(3, 1400.0),
                    HargaFragment.HargaTBS(4, 1500.0)
                )
            )
        )
    }

    private fun deduplicateRuntime(list: List<HargaFragment.DataTBS>): List<HargaFragment.DataTBS> {
        val map = mutableMapOf<Pair<String, String>, HargaFragment.DataTBS>()
        for (item in list) {
            val key = item.provinsi to item.periode
            map[key] = map[key]?.let { existing ->
                val mergedHarga = existing.hargaTBS.zip(item.hargaTBS) { a, b ->
                    HargaFragment.HargaTBS(a.umur, (a.harga + b.harga) / 2)
                }
                existing.copy(
                    cpoAverage = (existing.cpoAverage + item.cpoAverage) / 2,
                    intiSawitAverage = (existing.intiSawitAverage + item.intiSawitAverage) / 2,
                    hargaTBS = mergedHarga
                )
            } ?: item
        }
        return map.values.toList()
    }

    private fun setupChart(data: List<HargaFragment.DataTBS>) {
        val lineDataSets = ArrayList<ILineDataSet>()
        val colors = listOf(Color.GREEN, Color.BLUE, Color.RED, Color.MAGENTA)

        data.forEachIndexed { index, d ->
            val entries = d.hargaTBS.map { Entry(it.umur.toFloat(), it.harga.toFloat()) }
            val dataSet = LineDataSet(entries, "${d.provinsi} TBS")
            dataSet.color = colors.getOrElse(index) { Color.BLACK }
            dataSet.valueTextColor = Color.BLACK
            lineDataSets.add(dataSet)

            // Tambahkan CPO rata-rata sebagai garis
            val cpoEntry = listOf(Entry(0f, d.cpoAverage.toFloat()), Entry(20f, d.cpoAverage.toFloat()))
            val cpoSet = LineDataSet(cpoEntry, "${d.provinsi} CPO")
            cpoSet.color = colors.getOrElse(index) { Color.GRAY }
            cpoSet.enableDashedLine(10f, 5f, 0f)
            lineDataSets.add(cpoSet)
        }

        chart.data = LineData(lineDataSets)
        chart.invalidate()
    }

    inner class TbsAdapter(private val list: List<HargaFragment.DataTBS>) :
        RecyclerView.Adapter<TbsAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemTbsBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.tvProvinsi.text = item.provinsi
            holder.binding.tvPeriode.text = item.periode

            val sb = StringBuilder()
            item.hargaTBS.forEach {
                sb.append("${it.umur} Tahun: Rp ${"%,.2f".format(it.harga)}\n")
            }
            holder.binding.tvHargaTbs.text = sb.toString()
        }

        override fun getItemCount(): Int = list.size
    }
}