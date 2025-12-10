package com.yogitechnolabs.loginmanager.saloonapp


import android.util.Log
import com.google.gson.Gson
import com.yogitechnolabs.loginmanager.api.RetrofitClient
import com.yogitechnolabs.loginmanager.saloonapp.model.Employee
import com.yogitechnolabs.loginmanager.saloonapp.model.ResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeeApiHelper(
    private val token: String,
    private val signature: String
) {

    fun getEmployees(salonId: String, onResult: (List<Employee>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = RetrofitClient.api.getStaff(
                    endpoint = "ws/eazemysaloon/get-staff-details",
                    signature = signature,
                    authToken = token,
                    salonId = salonId
                )

                if (res.isSuccessful && !res.body().isNullOrEmpty()) {
                    val model = Gson().fromJson<ResponseModel<List<Employee>>>(
                        res.body(),
                        ResponseModel.listType<Employee>()
                    )
                    if (model.success) onResult(model.data)
                    else onResult(null)
                } else onResult(null)

            } catch (e: Exception) {
                Log.e("EMP_API", "GET Error: ${e.message}", e)
                onResult(null)
            }
        }
    }

    fun addEmployee(data: Map<String, Any>, onResult: (Employee?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = RetrofitClient.api.callApi(
                    endpoint = "eazemysaloon/staff",
                    signature = signature,
                    authToken = token,
                    data = data
                )

                if (res.isSuccessful && !res.body().isNullOrEmpty()) {
                    val model = Gson().fromJson<ResponseModel<Employee>>(
                        res.body(),
                        ResponseModel.singleType<Employee>()
                    )
                    onResult(model.data)
                } else onResult(null)
            } catch (e: Exception) {
                Log.e("EMP_API", "ADD Error: ${e.message}", e)
                onResult(null)
            }
        }
    }

    fun editEmployee(employeeId: String, data: Map<String, Any>, onResult: (Employee?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = RetrofitClient.api.callApi(
                    endpoint = "eazemysaloon/staff/$employeeId",
                    signature = signature,
                    authToken = token,
                    data = data
                )

                if (res.isSuccessful && !res.body().isNullOrEmpty()) {
                    val model = Gson().fromJson<ResponseModel<Employee>>(
                        res.body(),
                        ResponseModel.singleType<Employee>()
                    )
                    onResult(model.data)
                } else onResult(null)
            } catch (e: Exception) {
                Log.e("EMP_API", "EDIT Error: ${e.message}", e)
                onResult(null)
            }
        }
    }

    fun deleteEmployee(employeeId: String, onResult: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = RetrofitClient.api.deleteStaff(
                    endpoint = "eazemysaloon/staff/$employeeId",
                    signature = signature,
                    token = token
                )

                onResult(res.isSuccessful)

            } catch (e: Exception) {
                Log.e("EMP_API", "DELETE Error: ${e.message}", e)
                onResult(false)
            }
        }
    }

}
