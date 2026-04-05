package com.example.hyperlocalecom.data.model
data class User(
    val id: String,
    val role: String,
    val full_name: String?,
    val phone: String?,
    val email: String
)