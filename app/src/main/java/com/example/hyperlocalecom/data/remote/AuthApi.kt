package com.example.hyperlocalecom.data.remote

import com.example.hyperlocalecom.data.model.AddVariantRequest
import com.example.hyperlocalecom.data.model.LoginRequest
import com.example.hyperlocalecom.data.model.LoginResponse
import com.example.hyperlocalecom.data.model.ProductDetailResponse
import com.example.hyperlocalecom.data.model.ProductsResponse
import com.example.hyperlocalecom.data.model.StoreResponse
import com.example.hyperlocalecom.data.model.VariantRequest
import com.example.hyperlocalecom.data.model.VariantResponse
import com.example.hyperlocalecom.data.model.VariantUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
//
//interface AuthApi {
//    @PATCH("api/v1/store-owner/variants/{variant_id}")
//    suspend fun updateVariant(
//        @Path("variant_id") variantId: String,
//        @Header("Authorization") token: String,
//        @Body body: VariantUpdateRequest
//    ): VariantResponse
//
//    @POST("api/v1/auth/login")
//    suspend fun login(
//        @Body request: LoginRequest
//    ): LoginResponse
//
//    @GET("api/v1/store-owner/store")
//    suspend fun getStore(
//        @Header("Authorization") token: String
//    ): StoreResponse
//
////    @GET("api/v1/store-owner/products")
////    suspend fun getProducts(
////        @Header("Authorization") token: String
////    ): ProductsResponse
//    @GET("api/v1/store-owner/products")
//    suspend fun getProducts(
//        @Header("Authorization") token: String,
//        @Query("name") query: String? = null
//    ): ProductsResponse
//
//    @GET("api/v1/store-owner/products/{product_id}")
//    suspend fun getProductById(
//        @Path("product_id") productId: String,
//        @Header("Authorization") token: String
//    ): ProductDetailResponse
//
//    @POST("api/v1/store-owner/products/{product_id}/variants")
//    suspend fun addVariant(
//        @Path("product_id") productId: String,
//        @Header("Authorization") token: String,
//        @Body body: AddVariantRequest
//    ): VariantResponse
//
//    @DELETE("api/v1/store-owner/variants/{variant_id}")
//    suspend fun deleteVariant(
//        @Path("variant_id") variantId: String,
//        @Header("Authorization") token: String
//    ): Map<String, String>
//}

interface AuthApi {
    @PATCH("api/v1/store-owner/variants/{variant_id}")
    suspend fun updateVariant(
        @Path("variant_id") variantId: String,
        @Body body: VariantUpdateRequest
    ): VariantResponse

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("api/v1/store-owner/store")
    suspend fun getStore(): StoreResponse

    @GET("api/v1/store-owner/products")
    suspend fun getProducts(
        @Query("name") query: String? = null
    ): ProductsResponse

    @GET("api/v1/store-owner/products/{product_id}")
    suspend fun getProductById(
        @Path("product_id") productId: String
    ): ProductDetailResponse

    @POST("api/v1/store-owner/products/{product_id}/variants")
    suspend fun addVariant(
        @Path("product_id") productId: String,
        @Body body: AddVariantRequest
    ): VariantResponse

    @DELETE("api/v1/store-owner/variants/{variant_id}")
    suspend fun deleteVariant(
        @Path("variant_id") variantId: String
    ): Map<String, String>
}