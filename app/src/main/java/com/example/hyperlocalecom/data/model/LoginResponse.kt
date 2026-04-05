package com.example.hyperlocalecom.data.model
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: User
)