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
    val success: Boolean?,
    val message: String?,
    val data: UserData?
)

data class UserData(
    val id: Int?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val role: String?,
    val token: String?,        // <-- required
    val auth_token: String?    // <-- required
)


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("user")
    fun registerUser(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<SignupResponse>

    @Headers("Content-Type: application/json")
    @POST("user-login")
    fun loginUSer(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<SignupResponse>

}

