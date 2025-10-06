package com.example.petanisawit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
// import androidx.recyclerview.widget.LinearLayoutManager
// import androidx.recyclerview.widget.RecyclerView
// import com.example.sawitku.adapter.NewsAdapter
// import com.example.sawitku.model.News
// import com.example.sawitku.network.RetrofitClient
// import retrofit2.Call
// import retrofit2.Callback
// import retrofit2.Response
import com.example.sawitku.R

class Komunitas : Fragment() {

    // private lateinit var recyclerView: RecyclerView
    // private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_komunitas, container, false)

        // recyclerView = view.findViewById(R.id.recyclerViewNews)
        // recyclerView.layoutManager = LinearLayoutManager(context)

        // fetchNews()

        // Untuk sementara, tampilkan teks statis
        val textView = view.findViewById<TextView>(R.id.textViewPlaceholder)
        textView.text = "Ini halaman Konsultan"

        return view
    }

    /*
    private fun fetchNews() {
        RetrofitClient.api.getNews().enqueue(object : Callback<List<News>> {
            override fun onResponse(call: Call<List<News>>, response: Response<List<News>>) {
                if (response.isSuccessful) {
                    val newsList = response.body() ?: emptyList()
                    adapter = NewsAdapter(newsList)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<News>>, t: Throwable) {
                // Handle error
            }
        })
    }
    */
}