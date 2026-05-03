package com.example.hyperlocalecom.data.model

import com.google.gson.annotations.SerializedName

data class CloudinaryResponse(
    @SerializedName("secure_url")
    val secure_url: String,
    @SerializedName("public_id")
    val public_id: String
)