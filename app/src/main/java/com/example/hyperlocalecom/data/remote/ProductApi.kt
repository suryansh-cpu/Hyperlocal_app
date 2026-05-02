package com.example.hyperlocalecom.data.remote

import com.example.hyperlocalecom.data.model.ProductCreateRequest
import com.example.hyperlocalecom.data.model.ProductResponse
import com.example.hyperlocalecom.data.model.AddImageRequest
import com.example.hyperlocalecom.data.model.VariantRequest
import retrofit2.http.*

interface ProductApi {

    @POST("api/v1/store-owner/products")
    suspend fun createProduct(
        @Body body: ProductCreateRequest
    ): ProductResponse

    @POST("api/v1/store-owner/products/{id}/variants")
    suspend fun addVariant(
        @Path("id") productId: String,
        @Body variant: VariantRequest
    )

    @POST("api/v1/store-owner/products/{id}/images")
    suspend fun addImage(
        @Path("id") productId: String,
        @Body image: AddImageRequest
    )
}