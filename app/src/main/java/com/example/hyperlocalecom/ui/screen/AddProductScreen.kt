package com.example.hyperlocalecom.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hyperlocalecom.data.model.AddImageRequest
import com.example.hyperlocalecom.data.model.CloudinaryResponse
import com.example.hyperlocalecom.data.model.ProductCreateRequest
import com.example.hyperlocalecom.data.model.ProductData
import com.example.hyperlocalecom.data.remote.RetrofitInstance.productApi
import com.example.hyperlocalecom.data.repository.uploadImageToCloudinary
import com.example.hyperlocalecom.ui.components.CustomTextField
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var productData by remember { mutableStateOf(ProductData()) }
    var uploadedImages by remember { mutableStateOf<List<CloudinaryResponse>?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var newSize by remember { mutableStateOf("") }
    var newStock by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        productData = productData.copy(
            images = (productData.images + uris).toMutableList()
        )
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let {
                productData = productData.copy(
                    images = (productData.images + it).toMutableList()
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (productData.name.isBlank() || productData.price.isBlank()) {
                            Toast.makeText(context, "Please fill Name and Price", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            uploading = true
                            try {
                                // 1. Upload images
                                val results = mutableListOf<CloudinaryResponse>()
                                for (uri in productData.images) {
                                    val uploaded = uploadImageToCloudinary(context, uri)
                                    if (uploaded != null) results.add(uploaded)
                                }
                                uploadedImages = results

                                // 2. CREATE PRODUCT
                                val product = productApi.createProduct(
                                    ProductCreateRequest(
                                        name = productData.name,
                                        brand = productData.brand,
                                        description = productData.description,
                                        cloth_material = productData.material,
                                        price = productData.price.toDoubleOrNull() ?: 0.0
                                    )
                                )

                                val productId = product.id

                                // 3. ADD VARIANTS
                                productData.size.forEach { (size, stock) ->
                                    viewModel.addVariant(
                                        productId = productId,
                                        size = size,
                                        color = productData.color.ifBlank { "Default" },
                                        stock = stock,
                                        price = productData.price.toIntOrNull() ?: 0
                                    )
                                }

                                // 4. ADD IMAGES
                                results.forEachIndexed { index, img ->
                                    productApi.addImage(
                                        productId,
                                        AddImageRequest(
                                            image_url = img.secure_url,
                                            cloudinary_public_id = img.public_id,
                                            is_primary = index == productData.coverImageIndex,
                                            sort_order = index
                                        )
                                    )
                                }

                                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                                viewModel.fetchProducts()
                                navController.popBackStack()

                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                uploading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uploading
                ) {
                    Text(if (uploading) "Processing..." else "Add Product")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            item {
                Text("Media", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { imageLauncher.launch("image/*") }) { Text("Gallery") }
                    Button(onClick = {
                        val uri = createImageUri(context)
                        cameraImageUri.value = uri
                        cameraLauncher.launch(uri)
                    }) { Text("Camera") }
                }
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    items(productData.images.size) { index ->
                        Box(modifier = Modifier.size(120.dp).padding(4.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(productData.images[index]),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small)
                                    .clickable { productData = productData.copy(coverImageIndex = index) }
                            )
                            IconButton(
                                onClick = {
                                    val newList = productData.images.toMutableList()
                                    newList.removeAt(index)
                                    productData = productData.copy(images = newList)
                                },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp),
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item {
                SectionTitle("Product Details")
                CustomTextField("Product Name", productData.name) { productData = productData.copy(name = it) }
                CustomTextField("Brand", productData.brand) { productData = productData.copy(brand = it) }
                CustomTextField("Material", productData.material) { productData = productData.copy(material = it) }
                CustomTextField("Description", productData.description, multiLine = true) { productData = productData.copy(description = it) }
            }

            item {
                SectionTitle("Attributes & Price")
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Color", productData.color) { productData = productData.copy(color = it) }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Price", productData.price) { productData = productData.copy(price = it) }
                    }
                }
            }

            item {
                SectionTitle("Sizes & Stock")
                productData.size.forEach { (size, stock) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$size : $stock", modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            val updatedMap = productData.size.toMutableMap()
                            updatedMap.remove(size)
                            productData = productData.copy(size = updatedMap)
                        }) { Icon(Icons.Default.Close, null) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Size", newSize) { newSize = it.uppercase() }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        CustomTextField("Stock", newStock) { newStock = it }
                    }
                    TextButton(onClick = {
                        if (newSize.isNotBlank() && newStock.isNotBlank()) {
                            val updatedMap = productData.size.toMutableMap()
                            updatedMap[newSize] = newStock.toIntOrNull() ?: 0
                            productData = productData.copy(size = updatedMap)
                            newSize = ""; newStock = ""
                        }
                    }) { Text("Add") }
                }
            }
        }
    }
}

@Composable
fun TextButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) { content() }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
