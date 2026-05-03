package com.example.hyperlocalecom.data.remote

import com.example.hyperlocalecom.data.model.CloudinaryResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

object CloudinaryInstance {
    val api: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}

data class CloudinarySignRequest(
    val productName: String,
    val kind: String,
    val color: String? = null
)

data class CloudinarySignResponse(
    val cloudName: String,
    val apiKey: String,
    val timestamp: Long,
    val signature: String,
    val folder: String,
    val publicId: String
)

interface CloudinarySignApi {
    @POST("api/cloudinary/sign")
    suspend fun getSignature(@Body request: CloudinarySignRequest): CloudinarySignResponse
}

interface CloudinaryApi {
    @Multipart
    @POST("{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part apiKey: MultipartBody.Part,
        @Part timestamp: MultipartBody.Part,
        @Part signature: MultipartBody.Part,
        @Part folder: MultipartBody.Part,
        @Part publicId: MultipartBody.Part
    ): CloudinaryResponse
}
