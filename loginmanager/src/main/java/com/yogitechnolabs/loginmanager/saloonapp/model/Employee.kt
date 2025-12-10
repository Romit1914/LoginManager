package com.yogitechnolabs.loginmanager.saloonapp.model

import com.google.gson.annotations.SerializedName

data class Employee(
    @SerializedName("staff_id")
    val id: String? = null,
    @SerializedName("salon_id")
    val salonId: String? = null,
    @SerializedName("staff_name")
    val staffName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    @SerializedName("hire_date")
    val hireDate: String? = null,
    @SerializedName("is_active")
    val isActive: String? = null,
    var services: List<Service>? = null   // assign manually after parsing
)
