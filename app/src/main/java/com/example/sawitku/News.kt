//package com.example.sawitku.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.sawitku.R
//import com.example.sawitku.model.News
//
//class NewsAdapter(private val newsList: List<News>) :
//    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
//
//    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val title: TextView = itemView.findViewById(R.id.newsTitle)
//        val description: TextView = itemView.findViewById(R.id.newsDescription)
//        val image: ImageView = itemView.findViewById(R.id.newsImage)
//        val date: TextView = itemView.findViewById(R.id.newsDate)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
//        return NewsViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = newsList.size
//
//    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        val news = newsList[position]
//        holder.title.text = news.title
//        holder.description.text = news.description
//        holder.date.text = news.date
//        Glide.with(holder.itemView.context)
//            .load(news.imageUrl)
//            .into(holder.image)
//    }
//}