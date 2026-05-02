package com.example.hyperlocalecom.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import kotlin.jvm.java


object CloudinaryInstance {

    val api: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}
object CloudinarySignInstance {

    val api: CloudinarySignApi by lazy {
        Retrofit.Builder()
//            .baseUrl("https://urbanrack.onrender.com/") // ⚠️ IMPORTANT
            .baseUrl("https://api.cloudinary.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinarySignApi::class.java)
    }
}
data class CloudinarySignResponse(
    val cloudName: String,
    val apiKey: String,
    val timestamp: Long,
    val signature: String,
    val folder: String,
    val publicId: String
)

interface CloudinarySignApi {
    @GET("/api/cloudinary/sign")
    suspend fun getSignature(): CloudinarySignResponse
}
interface CloudinaryApi {

    @Multipart
    @POST("v1_1/{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,

        @Part file: MultipartBody.Part,

        @Part("api_key") apiKey: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("signature") signature: RequestBody,
        @Part("folder") folder: RequestBody,
        @Part("public_id") publicId: RequestBody
    ): Response<ResponseBody>
}
