package com.yogitechnolabs.loginmanager.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


// Signup / Login response body
data class SignupResponse(
    val success: Boolean?,
    val message: String?,
    val data: UserData?
)

data class UserData(
    val id: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val role: String?,
    val token: String?,
    val auth_token: String?
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: List<LoginUser>?
)

data class LoginUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val phone: String,
    val auth_token: String
)


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("eazemysaloon/users")
    fun registerUser(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<SignupResponse>

    @Headers("Content-Type: application/json")
    @POST("ws/eazemysaloon/user-login")
    fun loginUSer(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<LoginResponse>

}

