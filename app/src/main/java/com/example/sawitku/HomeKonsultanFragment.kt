package com.example.sawitku

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeKonsultanFragment : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        SharedData.initDummyData()

        val scrollView = ScrollView(requireContext())
        val root = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 48, 24, 24)
            setBackgroundColor(Color.parseColor("#F0F4F0"))
        }

        val title = TextView(requireContext()).apply {
            text = "Dashboard Konsultan"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#89A86C"))
            setPadding(0, 0, 0, 24)
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PetaniAdapter(SharedData.laporanKonsultan)
        }

        root.addView(title)
        root.addView(recyclerView)
        scrollView.addView(root)

        return scrollView
    }

    inner class PetaniAdapter(
        private val data: MutableList<SharedData.Laporan>
    ) : RecyclerView.Adapter<PetaniAdapter.VH>() {

        inner class VH(val card: CardView) : RecyclerView.ViewHolder(card) {
            val tvNama = TextView(requireContext())
            val tvDetail = TextView(requireContext())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val card = CardView(parent.context).apply {
                radius = 16f
                setCardBackgroundColor(Color.WHITE)
                useCompatPadding = true
            }

            val layout = LinearLayout(parent.context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 24, 24, 24)
            }

            val holder = VH(card)

            holder.tvNama.textSize = 16f
            holder.tvNama.setTextColor(Color.BLACK)

            holder.tvDetail.textSize = 14f
            holder.tvDetail.setTextColor(Color.DKGRAY)

            layout.addView(holder.tvNama)
            layout.addView(holder.tvDetail)
            card.addView(layout)

            return holder
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val l = data[position]
            holder.tvNama.text = "${l.namaPetani} - ${l.namaKebun}"
            holder.tvDetail.text =
                "Luas: ${l.luas} Ha\nTanggal: ${l.tanggal}\nStatus: ${l.status}"
        }

        override fun getItemCount(): Int = data.size
    }
}
