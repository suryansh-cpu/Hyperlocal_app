package com.example.hyperlocalecom.data.model

import com.google.gson.annotations.SerializedName

// 🔥 BASIC PRODUCT
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: String
)

// 🔥 IMAGE MODEL (IMPORTANT FIX)
data class ProductImage(
    @SerializedName("image_url")
    val imageUrl: String?
)

// 🔥 VARIANT MODEL (adjust fields if needed)
data class Variant(
    val id: String,
    val color: String?,
    val size: String,

    @SerializedName("stock_qty")
    val stock: Int
)

// 🔥 PRODUCT DETAIL RESPONSE (FINAL CORRECT)
data class ProductDetailResponse(
    val product: Product,
    val variants: List<Variant>,
    val images: List<ProductImage>
)