package com.example.hyperlocalecom.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ProductData(
    val name: String = "",
    val brand: String = "",
    val material: String = "",
    val description: String = "",

    val color: String = "",
//    val size: String = "",
    val price: String = "",
    val size: MutableMap<String, Int> = mutableMapOf(),

    val images: MutableList<Uri> = mutableListOf(),

    val coverImageIndex: Int = 0
)
data class ProductsResponse(
    val items: List<Product>
)

data class ProductResponse(
    val id: String
)
data class ProductCreateRequest(
    val category_id: Int = 1,
    val name: String,
    val brand: String?,
    val description: String?,
    val cloth_material: String?
)


//
//import com.google.gson.annotations.SerializedName
//
//// 🔥 BASIC PRODUCT
////data class Product(
////    val id: String,
////    val name: String,
////    val description: String?,
////    val price: String
////)
//
////// 🔥 IMAGE MODEL (IMPORTANT FIX)
////data class ProductImage(
////    @SerializedName("image_url")
////    val imageUrl: String?
////)
////
////// 🔥 VARIANT MODEL (adjust fields if needed)
////data class Variant(
////    val id: String,
////    val color: String?,
////    val size: String,
////
////    @SerializedName("stock_qty")
////    val stock: Int
////)
////
////// 🔥 PRODUCT DETAIL RESPONSE (FINAL CORRECT)
////data class ProductDetailResponse(
////    val product: Product,
////    val variants: List<Variant>,
////    val images: List<ProductImage>
////)
//data class Product(
//    val id: String,
//    val name: String,
//    val description: String?,
//    val price: String,
//
//    val brand: String?,
//
//    @SerializedName("image_url")
//    val imageUrl: String?,
//
//    @SerializedName("total_stock")
//    val totalStock: Int,
//
//    @SerializedName("variant_count")
//    val variantCount: Int
//)
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: String,
    /*
    * productData.images
    productData.name
    productData.brand
    productData.material //////
    productData.description
    productData.color
    productData.size
    productData.price
    productData.coverImageIndex
    * */
    val brand: String?,

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
//data class VariantRequest(
//    val size: String,
//    val color: String?,
//    val price: Double,
//
//    @SerializedName("stock_qty")
//    val stock_qty: Int
//)
//data class Variant(
//    val id: String,
//    val size: String,
//    val color: String?,
//
//    @SerializedName("stock_qty")
//    val stock: Int
//)
////data class Variant(
////    val id: String,
////    val color: String?,
////    val size: String,
////
////    @SerializedName("stock_qty")
////    val stock: Int
////)

data class ProductDetailResponse(
    val product: Product,
    val variants: List<VariantResponse>,
    val images: List<ProductImage>
)