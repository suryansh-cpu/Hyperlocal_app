package com.example.hyperlocalecom.data.model

import com.google.gson.annotations.SerializedName

data class AddVariantRequest(
    val size: String,
    val color: String,
    val price: Int,

    @SerializedName("stock_qty")
    val stockQty: Int
)
data class AddImageRequest(
    val image_url: String,
    val cloudinary_public_id: String,
    val is_primary: Boolean,
    val sort_order: Int
)
data class VariantRequest(
    val size: String,
    val color: String?,
    val price: Double,

    @SerializedName("stock_qty")
    val stock_qty: Int
)
data class VariantResponse(
    val id: String,
    val size: String,
    val color: String?,

    @SerializedName("stock_qty")
    val stock: Int
)
data class VariantUpdateRequest(
    val stock_qty: Int
)