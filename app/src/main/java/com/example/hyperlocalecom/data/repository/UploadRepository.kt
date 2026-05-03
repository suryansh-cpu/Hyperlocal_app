package com.example.hyperlocalecom.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hyperlocalecom.data.model.CloudinaryResponse
import com.example.hyperlocalecom.data.remote.CloudinaryInstance
import com.example.hyperlocalecom.data.remote.CloudinarySignRequest
import com.example.hyperlocalecom.data.remote.RetrofitInstance
import com.example.hyperlocalecom.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

suspend fun uploadImageToCloudinary(
    context: Context,
    uri: Uri,
    productName: String = "generic",
    kind: String = "generic",
    color: String? = null
): CloudinaryResponse? {

    return try {
        // 🔹 1. GET SIGNATURE
        val sign = RetrofitInstance.cloudinarySignApi.getSignature(
            CloudinarySignRequest(productName, kind, color)
        )
        Log.d("CLOUDINARY_DEBUG", "Signature received for: ${sign.publicId}")

        // 🔹 2. URI → FILE
        val file = uriToFile(context, uri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // 🔹 3. PREPARE PARTS
        val apiKey = sign.apiKey.toRequestBody("text/plain".toMediaTypeOrNull())
        val timestamp = sign.timestamp.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val signature = sign.signature.toRequestBody("text/plain".toMediaTypeOrNull())
        val folder = sign.folder.toRequestBody("text/plain".toMediaTypeOrNull())
        val publicId = sign.publicId.toRequestBody("text/plain".toMediaTypeOrNull())

        // 🔹 4. UPLOAD
        val response = CloudinaryInstance.api.uploadImage(
            cloudName = sign.cloudName,
            file = filePart,
            apiKey = apiKey,
            timestamp = timestamp,
            signature = signature,
            folder = folder,
            publicId = publicId
        )
        Log.d("CLOUDINARY_DEBUG", "Upload Success: ${response.secure_url}")
        response
    } catch (e: Exception) {
        Log.e("CLOUDINARY_DEBUG", "Upload Failed: ${e.message}")
        e.printStackTrace()
        null
    }
}
