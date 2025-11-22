package com.yogitechnolabs.loginmanager.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String
)

data class SignupResponse(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String,
    val auth_token: String,
    val id: String,
    val token: String
)


interface ApiService {

    @POST("user")
    fun registerUser(
        @Body body: SignupRequest
    ): Call<SignupResponse>
}