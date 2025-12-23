//// RetrofitInstance.kt
//package com.example.sawitku.network
//
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
//
//object RetrofitInstance {
//
//    private val moshi = Moshi.Builder()
//        .add(KotlinJsonAdapterFactory()) // reflection, no kapt needed
//        .build()
//
//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://your-api-base-url.com/api/v1/") // ganti sesuai API
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//    }
//
//    val api: NewsApiService by lazy {
//        retrofit.create(NewsApiService::class.java)
//    }
//}