package com.yogitechnolabs.loginmanager.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url


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
    val data: LoginUser
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
    @POST("ws/eazemysaloon/users/auth")
    fun loginUSer(
        @Header("X-API-SIGNATURE") signature: String,
        @Body body: Map<String, String>
    ): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST
    suspend fun callApi(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String? = null,
        @Header("X-AUTH-TOKEN") authToken: String? = null,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Response<String>

    @Headers("Content-Type: application/json")
    @GET
    suspend fun getApi(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String? = null,
        @Header("X-AUTH-TOKEN") authToken: String? = null,
    ): Response<String>


    @Headers("Content-Type: application/json")
    @GET
    suspend fun getStaff(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String? = null,
        @Header("X-AUTH-TOKEN") authToken: String? = null,
        @Query("salon_id") salonId: String
    ): Response<String>

    @Headers("Content-Type: application/json")
    @DELETE
    suspend fun deleteStaff(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String,
        @Header("X-AUTH-TOKEN") token: String?
    ): Response<String>

    @Headers("Content-Type: application/json")
    @PUT
    suspend fun editStaff(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String,
        @Header("X-AUTH-TOKEN") token: String?,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Response<String>

    @Headers("Content-Type: application/json")
    @GET
    suspend fun getApiData(
        @Url endpoint: String,
        @Header("X-API-SIGNATURE") signature: String? = null,
        @Header("X-AUTH-TOKEN") authToken: String? = null,
        @Query("salon_id") salonId: String
    ): Response<String>


}

