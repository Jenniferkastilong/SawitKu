package com.example.sawitku.fragment

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
import com.example.sawitku.adapter.NewsAdapter
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BeritaFragment : Fragment() {

    data class Article(
        val title: String,
        val description: String?,
        val url: String,
        val image: String?,
        val publishedAt: String
    )

    data class NewsResponse(
        val totalArticles: Int,
        val articles: List<Article>
    )

    interface ApiService {
        @GET("search")
        suspend fun getNews(
            @Query("q") query: String,
            @Query("lang") lang: String = "id",
            @Query("country") country: String = "id",
            @Query("max") max: Int = 20,
            @Query("apikey") apiKey: String
        ): NewsResponse
    }

    object RetrofitClient {
        private const val BASE_URL = "https://gnews.io/api/v4/"
        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    private val API_KEY = "dc81aca19fae687660e9d3f5524de76a"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_berita, container, false)
        val rv = v.findViewById<RecyclerView>(R.id.rv_berita)
        rv.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getNews(
                    query = "kelapa sawit",
                    apiKey = API_KEY
                )
                rv.adapter = NewsAdapter(response.articles, requireContext())

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memuat berita", Toast.LENGTH_SHORT).show()
            }
        }

        return v
    }
}
