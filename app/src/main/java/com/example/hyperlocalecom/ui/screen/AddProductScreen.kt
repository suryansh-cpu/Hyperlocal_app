package com.example.hyperlocalecom.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
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
    var mainInfo by remember { mutableStateOf(ProductData()) }
    val variants = remember { mutableStateListOf(ProductData()) }
    var uploading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (mainInfo.name.isBlank()) {
                            Toast.makeText(context, "Please fill Product Name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            uploading = true
                            try {
                                // 1. CREATE PRODUCT
                                val product = productApi.createProduct(
                                    ProductCreateRequest(
                                        name = mainInfo.name,
                                        brand = mainInfo.brand,
                                        description = mainInfo.description,
                                        cloth_material = mainInfo.material
                                    )
                                )
                                val productId = product.id

                                // 2. PROCESS EACH VARIANT
                                var imageUploadedCount = 0
                                variants.forEach { variantData ->
                                    // A. Upload images for this variant
                                    val uploadedResults = mutableListOf<CloudinaryResponse>()
                                    variantData.images.forEach { uri ->
                                        val uploaded = uploadImageToCloudinary(
                                            context = context,
                                            uri = uri,
                                            productName = mainInfo.name,
                                            kind = if (variantData.color.isNotBlank()) "color" else "primary",
                                            color = variantData.color.ifBlank { null }
                                        )
                                        if (uploaded != null) uploadedResults.add(uploaded)
                                    }

                                    // B. Add variant stock/size
                                    variantData.size.forEach { (size, stock) ->
                                        viewModel.addVariant(
                                            productId = productId,
                                            size = size,
                                            color = variantData.color.ifBlank { "Default" },
                                            stock = stock,
                                            price = variantData.price.toIntOrNull() ?: 0
                                        )
                                    }

                                    // C. Link images to product
                                    uploadedResults.forEach { img ->
                                        val isFirstImage = (imageUploadedCount == 0)
                                        productApi.addImage(
                                            productId,
                                            AddImageRequest(
                                                image_url = img.secure_url,
                                                cloudinary_public_id = img.public_id,
                                                is_primary = isFirstImage,
                                                sort_order = imageUploadedCount
                                            )
                                        )
                                        imageUploadedCount++
                                    }
                                }

                                Toast.makeText(context, "Product & all variants added!", Toast.LENGTH_SHORT).show()
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
                    Text(if (uploading) "Processing..." else "Add Product with ${variants.size} Variants")
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
                SectionTitle("Step 1: Core Product Info")
                CustomTextField("Product Name", mainInfo.name) { mainInfo = mainInfo.copy(name = it) }
                CustomTextField("Brand", mainInfo.brand) { mainInfo = mainInfo.copy(brand = it) }
                CustomTextField("Material", mainInfo.material) { mainInfo = mainInfo.copy(material = it) }
                CustomTextField("Description", mainInfo.description, multiLine = true) { mainInfo = mainInfo.copy(description = it) }
                Spacer(Modifier.height(24.dp))
            }

            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Step 2: Variants",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Button(
                        onClick = { variants.add(ProductData()) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add Another Color Variant")
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            itemsIndexed(variants) { index, variant ->
                VariantInputSection(
                    index = index,
                    variant = variant,
                    onUpdate = { variants[index] = it },
                    onRemove = { if (variants.size > 1) variants.removeAt(index) }
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VariantInputSection(
    index: Int,
    variant: ProductData,
    onUpdate: (ProductData) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    var newSize by remember { mutableStateOf("") }
    var newStock by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onUpdate(variant.copy(images = (variant.images + uris).toMutableList()))
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                onUpdate(variant.copy(images = (variant.images + uri).toMutableList()))
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Color Group #${index + 1}", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (index > 0) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Close, "Remove", tint = Color.Red)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            
            Text("Images for this Color", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) { 
                    Text("Gallery") 
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = { 
                        val uri = createImageUri(context)
                        cameraImageUri.value = uri
                        cameraLauncher.launch(uri)
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) { 
                    Text("Camera")
                }
            }
            
            if (variant.images.isNotEmpty()) {
                LazyRow(
                    Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(variant.images) { idx, uri ->
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (idx == variant.coverImageIndex) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onUpdate(variant.copy(coverImageIndex = idx)) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(if (idx == variant.coverImageIndex) 4.dp else 0.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            
                            // Delete Image Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .background(Color.Black.copy(0.6f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        val newList = variant.images.toMutableList()
                                        newList.removeAt(idx)
                                        onUpdate(variant.copy(images = newList))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            
                            if (idx == variant.coverImageIndex) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary.copy(0.8f))
                                        .padding(vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Main", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    CustomTextField("Color", variant.color) { onUpdate(variant.copy(color = it)) }
                }
                Box(Modifier.weight(1f)) {
                    CustomTextField("Price", variant.price) { onUpdate(variant.copy(price = it)) }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Stock per Size", style = MaterialTheme.typography.labelLarge)
            
            variant.size.forEach { (s, q) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(s, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Qty: $q", modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = {
                        val m = variant.size.toMutableMap()
                        m.remove(s)
                        onUpdate(variant.copy(size = m))
                    }, modifier = Modifier.size(24.dp)) { 
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) 
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(Modifier.weight(0.4f)) {
                    CustomTextField("Size", newSize) { newSize = it.uppercase() }
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(0.4f)) {
                    CustomTextField("Qty", newStock) { newStock = it }
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (newSize.isNotBlank() && newStock.isNotBlank()) {
                            val m = variant.size.toMutableMap()
                            m[newSize] = newStock.toIntOrNull() ?: 0
                            onUpdate(variant.copy(size = m))
                            newSize = ""; newStock = ""
                        }
                    },
                    modifier = Modifier.weight(0.2f)
                ) { 
                    Text("Add", fontWeight = FontWeight.Bold) 
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text, 
        style = MaterialTheme.typography.titleLarge, 
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
