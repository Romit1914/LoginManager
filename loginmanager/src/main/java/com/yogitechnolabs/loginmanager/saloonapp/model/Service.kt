package com.yogitechnolabs.loginmanager.saloonapp.model

data class Service(
    val id: String? = null,
    val salon_id: String? = null,
    val staff_id: String? =null,
    val name: String? = null,
    val duration_in_minutes: Int? =null,
    var price: String? = null,
    var base_price: String? = null,
    var custom_price: String? = null,
    val description: String,
    var isSelected: Boolean = false
)