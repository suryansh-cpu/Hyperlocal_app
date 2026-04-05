package com.example.hyperlocalecom.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.data.model.LoginRequest
import com.example.hyperlocalecom.data.model.LoginResponse
import com.example.hyperlocalecom.data.model.Product
import com.example.hyperlocalecom.data.model.ProductDetailResponse
import com.example.hyperlocalecom.data.model.StoreResponse
import com.example.hyperlocalecom.data.remote.RetrofitInstance
import com.example.hyperlocalecom.data.remote.RetrofitInstance.api
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    lateinit var tokenManager: TokenManager
    var loginState = mutableStateOf<LoginResponse?>(null)
    var error = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var storeData = mutableStateOf<StoreResponse?>(null)
    var products = mutableStateOf<List<Product>>(emptyList())
    var productDetails = mutableStateOf<ProductDetailResponse?>(null)

    fun fetchStore(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getStore("Bearer $token")
                storeData.value = response
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (LoginResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(
                    LoginRequest(email, password)
                )

                onSuccess(response)

            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun fetchProducts(token: String) {
        Log.d("PRODUCT_DEBUG", "API CALLED")

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProducts("Bearer $token")

                Log.d("PRODUCT_DEBUG", "RESPONSE: ${response.items}")

                products.value = response.items

            } catch (e: Exception) {
                Log.d("PRODUCT_DEBUG", "ERROR: ${e.message}")
            }
        }
    }

//    fun fetchProductDetails(productId: String, token: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getProductById(
//                    productId,
//                    "Bearer $token"
//                )
//                productDetails.value = response
//            } catch (e: Exception) {
//                error.value = e.message
//            }
//        }
//    }

//    var productDetails = mutableStateOf<ProductDetailResponse?>(null)
    var isLoadingProductDetails = mutableStateOf(false)

    fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                isLoadingProductDetails.value = true

                val token = "Bearer ${TokenManager.getToken()}"
                val response = RetrofitInstance.api.getProductById(productId, token)

                productDetails.value = response

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingProductDetails.value = false
            }
        }
    }

//    fun updateVariantStock(variantId: String, newStock: Int) {
//        viewModelScope.launch {
//            try {
//                val token = "Bearer ${TokenManager.getToken()}"
//
//                val response = RetrofitInstance.api.updateVariant(
//                    variantId,
//                    token,
//                    mapOf("stock" to newStock)
//                )
//
//                Log.d("UPDATE_API", "SUCCESS: $response")
//
//            } catch (e: Exception) {
//                Log.e("UPDATE_API", "ERROR: ${e.message}")
//            }
//        }
//    }
    fun updateVariantStock(
        variantId: String,
        newStock: Int
    ) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${TokenManager.getToken()}"

                val response = RetrofitInstance.api.updateVariant(
                    variantId,
                    token,
                    mapOf("stock_qty" to newStock) // 🔥 FIX KEY
                )

                Log.d("UPDATE_API", "UPDATED: ${response.stock}")

            } catch (e: Exception) {
                Log.e("UPDATE_API", "FAILED: ${e.message}")
            }
        }
    }
}