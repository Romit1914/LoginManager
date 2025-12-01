package com.yogitechnolabs.loginmanager.saloonapp.data

data class Employee(
    val id: String,
    val name: String,
    val profileUrl: String?,
    val services: List<Service>
)