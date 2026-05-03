package com.example.hyperlocalecom.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hyperlocalecom.data.model.AddImageRequest
import com.example.hyperlocalecom.data.model.ProductData
import com.example.hyperlocalecom.data.remote.RetrofitInstance.productApi
import com.example.hyperlocalecom.data.repository.uploadImageToCloudinary
import com.example.hyperlocalecom.ui.components.CustomTextField
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddVariantScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    productId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val productDetails by viewModel.productDetails
    val isLoadingDetails by viewModel.isLoadingProductDetails
    
    var productData by remember { mutableStateOf(ProductData()) }
    var variantImage by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Pre-fill data when details are loaded
    LaunchedEffect(productId) {
        viewModel.fetchProductDetails(productId)
    }

    LaunchedEffect(productDetails) {
        productDetails?.let { details ->
            productData = productData.copy(
                name = details.product.name,
                brand = details.product.brand ?: "",
                material = details.product.material ?: "",
                description = details.product.description ?: "",
                price = details.product.price ?: ""
            )
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        variantImage = uri
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { variantImage = it }
        }
    }

    if (isLoadingDetails) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    if (productData.color.isBlank()) {
                        Toast.makeText(context, "Color is required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        isProcessing = true
                        try {
                            // 1. Update Product Details (if changed)
                            viewModel.updateProduct(
                                productId,
                                productData.name,
                                productData.brand,
                                productData.material,
                                productData.description
                            )

                            // 2. Upload Image (if selected)
                            var imageUrl = ""
                            var publicId = ""
                            variantImage?.let { uri ->
                                val uploadResponse = uploadImageToCloudinary(
                                    context = context,
                                    uri = uri,
                                    productName = productData.name,
                                    kind = "color",
                                    color = productData.color
                                )
                                if (uploadResponse != null) {
                                    imageUrl = uploadResponse.secure_url
                                    publicId = uploadResponse.public_id
                                    
                                    // Add image to product
                                    productApi.addImage(
                                        productId,
                                        AddImageRequest(
                                            image_url = imageUrl,
                                            cloudinary_public_id = publicId,
                                            is_primary = false,
                                            sort_order = 1
                                        )
                                    )
                                }
                            }

                            // 3. Add Variant (using the first size in the map for simplicity, or we can add a loop)
                            productData.size.forEach { (size, stock) ->
                                viewModel.addVariant(
                                    productId = productId,
                                    size = size,
                                    color = productData.color,
                                    stock = stock,
                                    price = productData.price.toIntOrNull() ?: 0
                                )
                            }

                            Toast.makeText(context, "Variant added!", Toast.LENGTH_SHORT).show()
                            viewModel.fetchProductDetails(productId)
                            navController.popBackStack()

                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = !isProcessing
            ) {
                Text(if (isProcessing) "Processing..." else "Save Variant")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxWidth()
        ) {
            item {
                SectionTitle("New Variant Details")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (variantImage != null) {
                            Box(
                                modifier = Modifier.size(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(variantImage),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = { imageLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Gallery")
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val uri = createImageUri(context)
                                        cameraImageUri.value = uri
                                        cameraLauncher.launch(uri)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Camera")
                                }
                            }
                        }
                    }
                    if (variantImage != null) {
                        androidx.compose.material3.TextButton(onClick = { variantImage = null }) {
                            Text("Clear Image")
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("New Color", productData.color) { productData = productData.copy(color = it) }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Variant Price", productData.price) { productData = productData.copy(price = it) }
                    }
                }

                SectionTitle("Sizes & Stock")
                var newSize by remember { mutableStateOf("") }
                var newStock by remember { mutableStateOf("") }

                productData.size.forEach { (size, stock) ->
                    Text("$size : $stock", modifier = Modifier.padding(vertical = 4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Size", newSize) { newSize = it.uppercase() }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Stock", newStock) { newStock = it }
                    }
                    androidx.compose.material3.TextButton(onClick = {
                        if (newSize.isNotBlank() && newStock.isNotBlank()) {
                            val updatedMap = productData.size.toMutableMap()
                            updatedMap[newSize] = newStock.toIntOrNull() ?: 0
                            productData = productData.copy(size = updatedMap)
                            newSize = ""; newStock = ""
                        }
                    }) { Text("Add") }
                }

                SectionTitle("Edit Product (Shared Info)")
                CustomTextField("Product Name", productData.name) { productData = productData.copy(name = it) }
                CustomTextField("Brand", productData.brand) { productData = productData.copy(brand = it) }
                CustomTextField("Material", productData.material) { productData = productData.copy(material = it) }
                CustomTextField("Description", productData.description, multiLine = true) { productData = productData.copy(description = it) }
            }
        }
    }
}
