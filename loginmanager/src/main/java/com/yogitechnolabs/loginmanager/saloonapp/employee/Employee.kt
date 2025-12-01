package com.yogitechnolabs.loginmanager.saloonapp.employee

data class Employee(
    val id: String,
    var name: String,
    var phone: String,
    var role: String,
    var services: MutableList<ServiceItem> = mutableListOf() // <-- yeh add karo
)

data class ServiceItem(
    var serviceName: String,
    var price: String
)