package com.mupy.soundpy.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api {
    object ReqMusic {
        // https://soundpy.azurewebsites.net
        private const val BASE_URL = "https://c828-128-201-181-115.ngrok-free.app/"
        private val client = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        private val gson = GsonBuilder().setLenient().create()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        val service: YouTubeService by lazy {
            retrofit.create(YouTubeService::class.java)
        }
    }
}