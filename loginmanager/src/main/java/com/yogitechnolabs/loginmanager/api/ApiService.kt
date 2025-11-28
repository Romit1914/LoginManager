package com.yogitechnolabs.loginmanager.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// Signup request body
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String
)

// Signup / Login response body
data class SignupResponse(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: String? = null,
    val phone: String? = null,
    val auth_token: String? = null,
    val id: String? = null,
    val token: String? = null
)

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("user")
    fun registerUser(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<SignupResponse>
}

