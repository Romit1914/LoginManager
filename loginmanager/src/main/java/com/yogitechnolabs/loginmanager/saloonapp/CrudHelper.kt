package com.yogitechnolabs.loginmanager.saloonapp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yogitechnolabs.loginmanager.api.RetrofitClient
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.URLEncoder

class CrudHelper {

    companion object {

        /**
         * Generic GET with optional query params
         */
        fun <T> get(
            endpoint: String,
            signature: String,
            authToken: String,
            queryParams: Map<String, String>? = null,
            type: TypeToken<T>,
            onSuccess: (T) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fullEndpoint = if (queryParams.isNullOrEmpty()) {
                        endpoint
                    } else {
                        val query = queryParams.entries.joinToString("&") {
                            "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
                        }
                        "$endpoint?$query"
                    }

                    Log.d("CrudHelper_GET", "Calling: $fullEndpoint")

                    val res: Response<String> =
                        RetrofitClient.api.getApi(fullEndpoint, signature, authToken)

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
         * Generic POST (Add)
         */
        fun add(
            endpoint: String,
            signature: String,
            authToken: String,
            data: Any,
            onSuccess: (Map<String, Any>) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res = RetrofitClient.api.callApi(
                        endpoint, signature, authToken,
                        data as Map<String, @JvmSuppressWildcards Any>
                    )

                    val body = res.body()
                    if (res.isSuccessful && !body.isNullOrEmpty()) {
                        val parsed: Map<String, Any> =
                            Gson().fromJson(body, Map::class.java) as Map<String, Any>
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
         * Generic PUT/Update
         */
        fun update(
            endpoint: String,
            signature: String,
            authToken: String,
            data: Any,
            onSuccess: (Map<String, Any>) -> Unit,
            onError: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reqMap = data as? Map<String, Any>
                        ?: return@launch onError("Invalid data, Map<String, Any> expected")

                    val res = RetrofitClient.api.callApi(
                        endpoint, signature, authToken, reqMap
                    )

                    val body = res.body()
                    if (res.isSuccessful && !body.isNullOrEmpty()) {
                        val parsed: Map<String, Any> =
                            Gson().fromJson(body, Map::class.java) as Map<String, Any>
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
                    val res = RetrofitClient.api.deleteStaff(endpoint, signature, authToken)

                    withContext(Dispatchers.Main) {
                        if (res.isSuccessful) {
                            onSuccess()
                        } else {
                            onError("DELETE Failed: ${res.code()} ${res.errorBody()?.string()}")
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError("DELETE Exception: ${e.message}")
                    }
                }
            }
        }
    }
}
