package com.github.da_dogk.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://dadogk2.duckdns.org/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}



//원래 코드
//val retrofit = Retrofit.Builder()
//    .baseUrl("https://dadogk.duckdns.org/api/")
//    .addConverterFactory(GsonConverterFactory.create())
//    .build()
//
//val service = retrofit.create(LoginInterface::class.java)