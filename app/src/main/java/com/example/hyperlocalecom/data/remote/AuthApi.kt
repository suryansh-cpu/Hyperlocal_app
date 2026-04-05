package com.example.hyperlocalecom.data.remote

import com.example.hyperlocalecom.data.model.LoginRequest
import com.example.hyperlocalecom.data.model.LoginResponse
import com.example.hyperlocalecom.data.model.Product
import com.example.hyperlocalecom.data.model.ProductDetailResponse
import com.example.hyperlocalecom.data.model.ProductsResponse
import com.example.hyperlocalecom.data.model.StoreResponse
import com.example.hyperlocalecom.data.model.Variant
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @PATCH("api/v1/store-owner/variants/{variant_id}")
    suspend fun updateVariant(
        @Path("variant_id") variantId: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Variant

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

//    @GET("api/v1/store-owner/store")
//    suspend fun getStore(): StoreResponse
    @GET("api/v1/store-owner/store")
    suspend fun getStore(
        @Header("Authorization") token: String
    ): StoreResponse

//    @GET("api/v1/store-owner/products")
//    suspend fun getProducts(
//        @Header("Authorization") token: String
//    ): List<Product>
//    @GET("api/v1/store-owner/products")
//    suspend fun getProducts(): List<Product>


//    @GET("api/v1/store-owner/products")
//    suspend fun getProducts(
//        @Header("Authorization") token: String
//    ): List<Product>

    @GET("api/v1/store-owner/products")
    suspend fun getProducts(
        @Header("Authorization") token: String
    ): ProductsResponse

    @GET("api/v1/store-owner/products/{product_id}")
    suspend fun getProductById(
        @Path("product_id") productId: String,
        @Header("Authorization") token: String
    ): ProductDetailResponse
}