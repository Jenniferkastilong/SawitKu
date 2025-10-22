package com.example.sawitku.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sawitku.R
import com.example.sawitku.fragment.BeritaFragment
import com.example.sawitku.BeritaDetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(
    private val list: List<BeritaFragment.Article>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.txt_title)
        val image: ImageView = v.findViewById(R.id.img_news)
        val date: TextView = v.findViewById(R.id.txt_date)
        val desc: TextView = v.findViewById(R.id.txt_desc)
//        val divider: View = v.findViewById(R.id.divider)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val formatter = SimpleDateFormat("dd MMM yyyy | HH:mm", Locale("id"))
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.title
        holder.desc.text = item.description ?: "(Tidak ada ringkasan)"
        holder.date.text = formatDate(item.publishedAt)

        Glide.with(context)
            .load(item.image)
            .placeholder(R.drawable.placeholder)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BeritaDetailActivity::class.java)
            intent.putExtra("url", item.url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}