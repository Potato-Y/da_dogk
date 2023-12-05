package com.github.da_dogk.server

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object RetrofitClient {
    private const val BASE_URL = "https://dadogk2.duckdns.org/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createRetrofitInstance(jwtToken: String?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(jwtToken))
            .build()
    }


    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    private fun createOkHttpClient(jwtToken: String?): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        if (!jwtToken.isNullOrBlank()) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $jwtToken")
                    .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }
        }

        return httpClient.build()
    }
    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
            Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                try{
                    nextResponseBodyConverter.convert(value)
                }catch (e:Exception){
                    e.printStackTrace()
                    null
                }
            } else{
                null
            }
        }
    }
}