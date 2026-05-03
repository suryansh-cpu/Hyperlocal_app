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

suspend fun uploadImageToCloudinary(
    context: Context,
    uri: Uri,
    productName: String,
    kind: String,
    color: String? = null
): CloudinaryResponse? {

    // 🔹 1. GET SIGNATURE
    val sign = RetrofitInstance.cloudinarySignApi.getSignature(
        CloudinarySignRequest(productName, kind, color)
    )
    Log.d("CLOUDINARY_DEBUG", "Signature received for: ${sign.publicId}")

    // 🔹 2. URI → FILE
    val file = uriToFile(context, uri)
    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

    // 🔹 3. PREPARE PARTS (using MultipartBody.Part to avoid Gson quoting strings)
    val apiKeyPart = MultipartBody.Part.createFormData("api_key", sign.apiKey)
    val timestampPart = MultipartBody.Part.createFormData("timestamp", sign.timestamp.toString())
    val signaturePart = MultipartBody.Part.createFormData("signature", sign.signature)
    val folderPart = MultipartBody.Part.createFormData("folder", sign.folder)
    val publicIdPart = MultipartBody.Part.createFormData("public_id", sign.publicId)

    // 🔹 4. UPLOAD
    return try {
        val response = CloudinaryInstance.api.uploadImage(
            cloudName = sign.cloudName,
            file = filePart,
            apiKey = apiKeyPart,
            timestamp = timestampPart,
            signature = signaturePart,
            folder = folderPart,
            publicId = publicIdPart
        )
        Log.d("CLOUDINARY_DEBUG", "Upload Success: ${response.secure_url}")
        response
    } catch (e: Exception) {
        Log.e("CLOUDINARY_DEBUG", "Upload Failed: ${e.message}")
        e.printStackTrace()
        throw e
    }
}
