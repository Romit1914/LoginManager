package com.yogitechnolabs.loginmanager.saloonapp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yogitechnolabs.loginmanager.api.RetrofitClient
import retrofit2.Response

class CrudHelper {

    companion object {

        /**
         * Generic GET
         * @param endpoint API endpoint
         * @param signature Header signature
         * @param authToken Auth token
         * @param onSuccess Success callback with parsed data
         * @param onError Error callback with message
         */
        fun <T> get(
            endpoint: String,
            signature: String,
            authToken: String,
            type: TypeToken<T>,
            onSuccess: (T) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res: Response<String> =
                        RetrofitClient.api.getApi(endpoint, signature, authToken)
                    val body = res.body()
                    if (res.isSuccessful && !body.isNullOrEmpty()) {
                        val data: T = Gson().fromJson(body, type.type)
                        onSuccess(data)
                    } else {
                        onError("GET Failed: ${res.code()} ${res.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    onError("GET Exception: ${e.message}")
                }
            }
        }

        /**
         * Generic CREATE/POST
         */
        fun <T> add(
            endpoint: String,
            signature: String,
            authToken: String,
            data: Any,
            type: TypeToken<T>,
            onSuccess: (T) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res: Response<String> = RetrofitClient.api.callApi(
                        endpoint, signature, authToken,
                        data as Map<String, @JvmSuppressWildcards Any>
                    )
                    val body = res.body()
                    if (res.isSuccessful && !body.isNullOrEmpty()) {
                        val parsed: T = Gson().fromJson(body, type.type)
                        onSuccess(parsed)
                    } else {
                        onError("ADD Failed: ${res.code()} ${res.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    onError("ADD Exception: ${e.message}")
                }
            }
        }

        /**
         * Generic UPDATE/PUT
         */
        fun <T> update(
            endpoint: String,
            signature: String,
            authToken: String,
            data: Any,
            type: TypeToken<T>,
            onSuccess: (T) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res: Response<String> = RetrofitClient.api.callApi(
                        endpoint, signature, authToken,
                        data as Map<String, @JvmSuppressWildcards Any>
                    )
                    val body = res.body()
                    if (res.isSuccessful && !body.isNullOrEmpty()) {
                        val parsed: T = Gson().fromJson(body, type.type)
                        onSuccess(parsed)
                    } else {
                        onError("UPDATE Failed: ${res.code()} ${res.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    onError("UPDATE Exception: ${e.message}")
                }
            }
        }

        /**
         * Generic DELETE
         */
        fun delete(
            endpoint: String,
            signature: String,
            authToken: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res: Response<String> =
                        RetrofitClient.api.deleteStaff(endpoint, signature, authToken)
                    if (res.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("DELETE Failed: ${res.code()} ${res.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    onError("DELETE Exception: ${e.message}")
                }
            }
        }
    }
}
