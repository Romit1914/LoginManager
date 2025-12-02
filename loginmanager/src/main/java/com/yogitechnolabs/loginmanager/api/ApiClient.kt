package com.yogitechnolabs.loginmanager.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.eazemyapi.com/")
        .addConverterFactory(GsonConverterFactory.create())  // Must be first for JSON
        .addConverterFactory(ScalarsConverterFactory.create())  // optional, for raw string responses
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
