package com.example.pharmacyarg.model.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by juanbrusco on 02/09/2020.
 */
object RetrofitClient {

    private const val BASE_URL = "https://pharmacy-admin-arg.herokuapp.com/api/"
    private const val TOKEN = "5be1fcc07510db3da52774ef2211261a0eddaaab"

    private const val AUTH =
        "Token $TOKEN"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", AUTH)
                .method(original.method, original.body)

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

}