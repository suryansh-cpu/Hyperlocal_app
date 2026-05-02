package com.example.hyperlocalecom.data.repository

import android.content.Context
import android.net.Uri
import com.example.hyperlocalecom.data.model.CloudinaryResponse
import com.example.hyperlocalecom.data.remote.CloudinaryInstance
import com.example.hyperlocalecom.data.remote.CloudinarySignInstance
import com.example.hyperlocalecom.utils.uriToFile
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun uploadImageToCloudinary(
    context: Context,
    uri: Uri
): CloudinaryResponse? {

    try {
        // 🔹 1. GET SIGNATURE
        val sign = CloudinarySignInstance.api.getSignature()

        // 🔹 2. URI → FILE
        val file = uriToFile(context, uri)

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        val body = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestFile
        )

        // 🔹 3. REQUIRED PARAMS
        val apiKey = sign.apiKey.toRequestBody("text/plain".toMediaTypeOrNull())
        val timestamp = sign.timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val signature = sign.signature.toRequestBody("text/plain".toMediaTypeOrNull())
        val folder = sign.folder.toRequestBody("text/plain".toMediaTypeOrNull())
        val publicId = sign.publicId.toRequestBody("text/plain".toMediaTypeOrNull())

        // 🔹 4. UPLOAD
        val response = CloudinaryInstance.api.uploadImage(
            sign.cloudName,
            body,
            apiKey,
            timestamp,
            signature,
            folder,
            publicId
        )

        if (response.isSuccessful) {
            val json = response.body()?.string()
            return Gson().fromJson(json, CloudinaryResponse::class.java)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}
//suspend fun uploadImageToCloudinary(
//    context: Context,
//    uri: Uri
//): CloudinaryResponse? {
//
//    val file = uriToFile(context, uri)
//
//    val requestFile = RequestBody.create(
//        "image/*".toMediaTypeOrNull(),
//        file
//    )
//
//    val body = MultipartBody.Part.createFormData(
//        "file",
//        file.name,
//        requestFile
//    )
//
//    val preset = MultipartBody.Part.createFormData(
//        "upload_preset",
//        "ml_default"
//    )
//
//    val response = CloudinaryInstance.api.uploadImage(body, preset)
//
//    if (response.isSuccessful) {
//        val json = response.body()?.string()
//        return Gson().fromJson(json, CloudinaryResponse::class.java)
//    }
//
//    return null
//}