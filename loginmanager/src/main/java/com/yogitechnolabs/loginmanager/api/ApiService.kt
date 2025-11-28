package com.yogitechnolabs.loginmanager.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("user")
    fun registerUser(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("role") role: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<SignupResponse>

}