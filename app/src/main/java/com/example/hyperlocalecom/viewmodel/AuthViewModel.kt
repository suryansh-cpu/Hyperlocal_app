package com.example.hyperlocalecom.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hyperlocalecom.data.model.AddVariantRequest
import com.example.hyperlocalecom.data.model.LoginRequest
import com.example.hyperlocalecom.data.model.LoginResponse
import com.example.hyperlocalecom.data.model.Product
import com.example.hyperlocalecom.data.model.ProductDetailResponse
import com.example.hyperlocalecom.data.model.StoreResponse
import com.example.hyperlocalecom.data.model.VariantResponse
import com.example.hyperlocalecom.data.model.VariantUpdateRequest
import com.example.hyperlocalecom.data.remote.RetrofitInstance.api
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var error = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var storeData = mutableStateOf<StoreResponse?>(null)
    var products = mutableStateOf<List<Product>>(emptyList())
    var productDetails = mutableStateOf<ProductDetailResponse?>(null)
    var isLoadingProductDetails = mutableStateOf(false)

    fun fetchStore() {
        viewModelScope.launch {
            try {
                val response = api.getStore()
                storeData.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Store Fetch Failed: ${e.message}")
                error.value = e.message
            }
        }
    }

    fun fetchProducts(query: String? = null) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = api.getProducts(query)
                Log.d("API_DEBUG", "Products received: ${response.items.size}")
                products.value = response.items
            } catch (e: Exception) {
                Log.e("API_ERROR", "Products Fetch Failed: ${e.message}")
                e.printStackTrace()
                error.value = "Failed to load products: ${e.message}"
            } finally {
                isLoading.value = false
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
                val response = api.login(
                    LoginRequest(email, password)
                )
                onSuccess(response)
            } catch (e: Exception) {
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                isLoadingProductDetails.value = true
                val response = api.getProductById(productId)
                productDetails.value = response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Product Details Failed: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoadingProductDetails.value = false
            }
        }
    }

    fun updateVariantStock(
        variantId: String,
        newStock: Int
    ) {
        viewModelScope.launch {
            try {
                val response = api.updateVariant(
                    variantId,
                    VariantUpdateRequest(newStock)
                )
                Log.d("UPDATE_API", "UPDATED: ${response.stock}")
            } catch (e: Exception) {
                Log.e("UPDATE_API", "FAILED: ${e.message}")
            }
        }
    }

    suspend fun updateProduct(
        productId: String,
        name: String,
        brand: String?,
        material: String?,
        description: String?
    ) {
        try {
            com.example.hyperlocalecom.data.remote.RetrofitInstance.productApi.updateProduct(
                productId,
                com.example.hyperlocalecom.data.model.ProductCreateRequest(
                    name = name,
                    brand = brand,
                    cloth_material = material,
                    description = description
                )
            )
        } catch (e: Exception) {
            Log.e("API_ERROR", "Update Product Failed: ${e.message}")
            throw e
        }
    }

    suspend fun addVariant(
        productId: String,
        size: String,
        color: String,
        stock: Int,
        price: Int
    ): VariantResponse? {
        return try {
            api.addVariant(
                productId,
                AddVariantRequest(
                    size = size,
                    color = color,
                    price = price,
                    stockQty = stock
                )
            )
        } catch (e: Exception) {
            Log.e("API_ERROR", "Add Variant Failed: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun deleteVariant(variantId: String) {
        viewModelScope.launch {
            try {
                api.deleteVariant(variantId)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Delete Variant Failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
