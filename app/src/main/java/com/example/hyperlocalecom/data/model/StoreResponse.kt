package com.example.hyperlocalecom.data.model

data class StoreResponse(
    val id: String,
    val owner_id: String,
    val name: String,
    val address: String,
    val lat_lng: String?,   // nullable
    val commission_rate: String,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)