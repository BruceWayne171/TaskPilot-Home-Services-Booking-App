package com.example.taskpilot
import com.example.taskpilot.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Make sure this is the base URL of your PHP project!
    private const val BASE_URL = "http://10.0.2.2/API/"

    // Use 'lazy' to ensure the Retrofit instance is created only when it's first needed.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}