package com.promecarus.storyapp.data.remote

import com.promecarus.storyapp.BuildConfig.BASE_URL
import com.promecarus.storyapp.BuildConfig.DEBUG
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory.create

object ApiConfig {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (DEBUG) BODY else NONE
    }
    private val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    val apiService: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(create()).client(client).build()
            .create(ApiService::class.java)
    }
}
