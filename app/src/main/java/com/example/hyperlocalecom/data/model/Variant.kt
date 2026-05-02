package com.example.hyperlocalecom.data.model

import com.google.gson.annotations.SerializedName

data class AddVariantRequest(
    val size: String,
    val color: String?,
    val price: Int,

    @SerializedName("stock_qty")
    val stockQty: Int
)

data class AddImageRequest(
    @SerializedName("image_url")
    val image_url: String,
    @SerializedName("cloudinary_public_id")
    val cloudinary_public_id: String,
    @SerializedName("is_primary")
    val is_primary: Boolean,
    @SerializedName("sort_order")
    val sort_order: Int
)

data class VariantRequest(
    val size: String,
    val color: String?,
    val price: Int,

    @SerializedName("stock_qty")
    val stock_qty: Int
)

data class VariantResponse(
    val id: String,
    val size: String,
    val color: String?,
    val price: Int?, // Added price field

    @SerializedName("stock_qty")
    val stock: Int
)

data class VariantUpdateRequest(
    @SerializedName("stock_qty")
    val stock_qty: Int
)
