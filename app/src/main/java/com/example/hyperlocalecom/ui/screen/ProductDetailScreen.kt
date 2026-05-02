package com.example.hyperlocalecom.ui.screen

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hyperlocalecom.data.model.VariantResponse
import com.example.hyperlocalecom.ui.components.VariantCard
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: AuthViewModel
) {
    val productDetails by viewModel.productDetails
    val isLoading by viewModel.isLoadingProductDetails

    LaunchedEffect(productId) {
        viewModel.fetchProductDetails(productId)
    }

    if (isLoading || productDetails == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val data = productDetails!!
    val variantsState = remember {
        mutableStateListOf(*data.variants.toTypedArray())
    }
    val product = data.product

    var isUpdating by remember { mutableStateOf(false) }

    var selectedColorVariants by remember {
        mutableStateOf<List<VariantResponse>?>(null)
    }

    var selectedColorName by remember { mutableStateOf<String?>(null) }

    // 🔥 FIX 1: Improved Image Selection Logic (fallback to product.imageUrl)
    var selectedImage by remember(data.images, product.imageUrl) {
        val firstGalleryImage = data.images.firstOrNull { !it.imageUrl.isNullOrBlank() }?.imageUrl
        mutableStateOf(firstGalleryImage ?: product.imageUrl ?: "")
    }

    // 🔥 FIX 2: Calculate Display Price from variants if main price is null
    val displayPrice = remember(product.price, variantsState.toList()) {
        if (!product.price.isNullOrBlank() && product.price != "null") {
            product.price
        } else {
            // Pick the first variant's price if the product's main price is missing
            variantsState.firstOrNull { it.price != null }?.price?.toString() ?: "--"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔥 IMAGES SECTION
        item {
            if (selectedImage.isNotBlank()) {
                AsyncImage(
                    model = selectedImage,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (data.images.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(data.images) { image ->
                        if (!image.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = image.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedImage = image.imageUrl!!
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔥 PRODUCT INFO
        item {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Brand: ${product.brand ?: "--"}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = "Material: ${product.material ?: "--"}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = "Price: ₹$displayPrice",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔥 VARIANTS SECTION
        val grouped = variantsState.groupBy { it.color }
        grouped.forEach { (color, list) ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color: ${color ?: "Default"}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Edit",
                        modifier = Modifier
                            .clickable {
                                selectedColorVariants = list
                                selectedColorName = color
                            }
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            items(list) { variant ->
                VariantCard(
                    variant = variant,
                    onIncrease = { v ->
                        if (isUpdating) return@VariantCard
                        isUpdating = true
                        val index = variantsState.indexOf(v)
                        if (index != -1) {
                            val newStock = v.stock + 1
                            variantsState[index] = variantsState[index].copy(stock = newStock)
                            viewModel.updateVariantStock(v.id, newStock)
                        }
                        isUpdating = false
                    },
                    onDecrease = { v ->
                        val index = variantsState.indexOf(v)
                        if (index != -1 && v.stock > 0) {
                            val newStock = v.stock - 1
                            variantsState[index] = variantsState[index].copy(stock = newStock)
                            viewModel.updateVariantStock(v.id, newStock)
                        }
                    }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Description", style = MaterialTheme.typography.titleMedium)
            Text(
                text = product.description ?: "No description available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }

    if (selectedColorVariants != null) {
        AlertDialog(
            onDismissRequest = {
                selectedColorVariants = null
                selectedColorName = null
            },
            confirmButton = {},
            text = {
                val editedVariants = remember {
                    selectedColorVariants!!.map { it.copy() }.toMutableStateList()
                }
                val originalVariants = remember {
                    selectedColorVariants!!.map { it.copy() }
                }
                var newSize by remember { mutableStateOf("") }
                val colorName = selectedColorName

                Column {
                    Text(text = "Edit ${colorName ?: ""}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    editedVariants.forEachIndexed { index, variant ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Size: ${variant.size}")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("-", modifier = Modifier.clickable {
                                    if (variant.stock > 0) {
                                        editedVariants[index] = variant.copy(stock = variant.stock - 1)
                                    }
                                }.padding(8.dp))
                                Text("${variant.stock}")
                                Text("+", modifier = Modifier.clickable {
                                    editedVariants[index] = variant.copy(stock = variant.stock + 1)
                                }.padding(8.dp))
                                Text("Delete", modifier = Modifier.clickable {
                                    editedVariants.removeAt(index)
                                }.padding(start = 12.dp), color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.TextField(
                            value = newSize,
                            onValueChange = { newSize = it.uppercase() },
                            label = { Text("Size") },
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "+ Add",
                            modifier = Modifier.clickable {
                                if (newSize.isNotBlank()) {
                                    editedVariants.add(VariantResponse(id = "", color = colorName, size = newSize, stock = 0, price = product.price?.toIntOrNull()))
                                    newSize = ""
                                }
                            }.padding(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cancel", modifier = Modifier.clickable {
                            selectedColorVariants = null
                            selectedColorName = null
                        }.padding(8.dp))
                        Text("Save", modifier = Modifier.clickable {
                            // ✅ Sync UI
                            variantsState.removeAll { it.color == colorName }
                            variantsState.addAll(editedVariants)

                            selectedColorVariants = null
                            selectedColorName = null

                            // ✅ Sync API
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                editedVariants.forEach { updated ->
                                    if (updated.id.isNotEmpty()) {
                                        viewModel.updateVariantStock(updated.id, updated.stock)
                                    } else {
                                        viewModel.addVariant(
                                            product.id, 
                                            updated.size, 
                                            colorName ?: "Default", 
                                            updated.stock, 
                                            product.price?.toDoubleOrNull()?.toInt() ?: 0
                                        )
                                    }
                                }
                                originalVariants.forEach { old ->
                                    if (editedVariants.none { it.id == old.id } && old.id.isNotEmpty()) {
                                        viewModel.deleteVariant(old.id)
                                    }
                                }
                                viewModel.fetchProductDetails(product.id)
                            }
                        }.padding(8.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        )
    }
}
