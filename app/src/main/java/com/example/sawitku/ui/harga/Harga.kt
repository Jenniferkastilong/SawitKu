package com.example.sawitku.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sawitku.databinding.ItemTbsBinding

class HargaFragment : Fragment() {

    data class HargaTBS(
        val umur: Int,
        val harga: Double
    )

    data class DataTBS(
        val provinsi: String,
        val periode: String,
        val cpoAverage: Double,
        val intiSawitAverage: Double,
        val hargaTBS: List<HargaTBS>
    )

    class TbsAdapter(private val list: List<DataTBS>) :
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

//            holder.binding.tvCpo.text = "Rp ${"%,.2f".format(item.cpoAverage)}"
//            holder.binding.tvKernel.text = "Harga Rata-rata Inti Sawit: Rp ${"%,.2f".format(item.intiSawitAverage)}"

            val sb = StringBuilder()
            item.hargaTBS.forEach {
                sb.append("${it.umur} Tahun: Rp ${"%,.2f".format(it.harga)}\n")
            }
            holder.binding.tvHargaTbs.text = sb.toString()
        }

        override fun getItemCount(): Int = list.size
    }
}