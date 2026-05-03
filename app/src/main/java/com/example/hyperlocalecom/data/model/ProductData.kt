package com.example.hyperlocalecom.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ProductData(
    val name: String = "",
    val brand: String = "",
    val material: String = "",
    val description: String = "",
    val color: String = "",
    val price: String = "",
    val size: MutableMap<String, Int> = mutableMapOf(),
    val images: MutableList<Uri> = mutableListOf(),
    val coverImageIndex: Int = 0
)

data class ProductsResponse(
    @SerializedName("items")
    val items: List<Product>
)

data class ProductResponse(
    @SerializedName("id")
    val id: String
)

data class ProductCreateRequest(
    @SerializedName("category_id")
    val category_id: Int = 1,
    @SerializedName("name")
    val name: String,
    @SerializedName("brand")
    val brand: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("cloth_material")
    val cloth_material: String?
)

data class Product(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("brand")
    val brand: String?,
    @SerializedName("cloth_material")
    val material: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("total_stock")
    val totalStock: Int,
    @SerializedName("variant_count")
    val variantCount: Int
)

data class ProductImage(
    @SerializedName("image_url")
    val imageUrl: String?
)

data class ProductDetailResponse(
    @SerializedName("product")
    val product: Product,
    @SerializedName("variants")
    val variants: List<VariantResponse>,
    @SerializedName("images")
    val images: List<ProductImage>
)
