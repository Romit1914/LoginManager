package com.yogitechnolabs.loginmanager.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://api.eazemyapi.com/"

    private val okHttp = okhttp3.OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("X-API-SIGNATURE", "d3bfa8b9b834a6497dd8fc0fcfed9f695e17688b1a2b3297d788755e796216bf")
                .method(original.method, original.body)
            chain.proceed(requestBuilder.build())
        }.build()

    val client: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
