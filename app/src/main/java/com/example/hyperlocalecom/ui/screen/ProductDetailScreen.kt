package com.example.hyperlocalecom.ui.screen

import android.util.Log
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

    LaunchedEffect(Unit) {
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

    var selectedImage: String? by remember {
        mutableStateOf(data.images.firstOrNull()?.imageUrl ?: "")
    }

    Row(modifier = Modifier.fillMaxSize()) {

        // 🔥 LEFT - IMAGES
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {

            AsyncImage(
                model = selectedImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow {
                items(data.images) { image ->
                    AsyncImage(
//                        model = image.imageUrl,
                        model = "http://10.0.2.2:8000${image.imageUrl}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                            .clickable {
                                selectedImage = image.imageUrl
                            }
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1.5f)
                .padding(16.dp)
        ) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Log.d("IMAGE_DEBUG", data.images.toString())
            val grouped = variantsState.groupBy { it.color }
            if (data.images.isNotEmpty()) {

                AsyncImage(
                    model = "http://10.0.2.2:8000${data.images[0].imageUrl}",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            LazyColumn {

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
                                text = "Color: $color",
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

                                    variantsState[index] =
                                        variantsState[index].copy(stock = newStock)

                                    viewModel.updateVariantStock(v.id, newStock)
                                }

                                isUpdating = false
                            },

                            onDecrease = { v ->
                                val index = variantsState.indexOf(v)
                                if (index != -1 && v.stock > 0) {
                                    val newStock = v.stock - 1

                                    variantsState[index] =
                                        variantsState[index].copy(stock = newStock)

                                    viewModel.updateVariantStock(v.id, newStock) // 🔥 API CALL
                                }
                            },

//                            onEdit = {}
                        )
                    }
                }
            }
        }
    }
    if (selectedColorVariants != null) {
        AlertDialog(
            onDismissRequest = {
                selectedColorVariants = null
                /***
                 * hi
                 */
                selectedColorName = null
            },
            confirmButton = {},
            text = {

                val editedVariants = remember {
                    selectedColorVariants!!.map {
                        it.copy(stock = it.stock)
                    }.toMutableStateList()
                }
                val originalVariants = remember {
                    selectedColorVariants!!.map { it.copy() }
                }
                var newSize by remember { mutableStateOf("") }
                val colorName = selectedColorName
                Column {

                    Text(
                        text = "Edit $colorName",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    editedVariants.forEachIndexed { index, variant ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text("Size: ${variant.size}")

                            Row {

                                Text(
                                    "-",
                                    modifier = Modifier
                                        .clickable {
                                            if (variant.stock > 0) {
                                                editedVariants[index] =
                                                    variant.copy(stock = variant.stock - 1)
                                            }
                                        }
                                        .padding(8.dp)
                                )

                                Text("${variant.stock}")

                                Text(
                                    "+",
                                    modifier = Modifier
                                        .clickable {
                                            editedVariants[index] =
                                                variant.copy(stock = variant.stock + 1)
                                        }
                                        .padding(8.dp)
                                )

                                Text(
                                    "Delete",
                                    modifier = Modifier
                                        .clickable {
                                            editedVariants.removeAt(index)
                                        }
                                        .padding(start = 12.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // ✅ FIXED ADD SIZE INPUT
                    Row {
                        androidx.compose.material3.TextField(
                            value = newSize,
                            onValueChange = { newSize = it.uppercase() },
                            label = { Text("Size") },
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            "+ Add",
                            modifier = Modifier
                                .clickable {
                                    if (newSize.isNotBlank()) {
                                        editedVariants.add(
                                            VariantResponse(
                                                id = "",
                                                color = colorName,
                                                size = newSize,
                                                stock = 0
                                            )
                                        )
                                        newSize = ""
                                    }
                                }
                                .padding(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            "Cancel",
                            modifier = Modifier
                                .clickable {
                                    selectedColorVariants = null
                                    selectedColorName = null
                                }
                                .padding(8.dp)
                        )
                        Text(
                            "Save",
                            modifier = Modifier.clickable {

                                // ✅ 1. UPDATE UI IMMEDIATELY
                                val index = variantsState.indexOfFirst { it.color == colorName }

                                variantsState.removeAll { it.color == colorName }
                                variantsState.addAll(index, editedVariants)

                                selectedColorVariants = null
                                selectedColorName = null

                                // ✅ 2. RUN API IN BACKGROUND
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
                                    .launch {

                                        // 🔥 ADD + UPDATE
                                        editedVariants.forEach { updated ->

                                            val existing =
                                                variantsState.find { it.id == updated.id }

                                            if (existing != null && updated.id.isNotEmpty()) {
                                                if (existing.stock != updated.stock) {
                                                    viewModel.updateVariantStock(
                                                        updated.id,
                                                        updated.stock
                                                    )
                                                }
                                            } else {
                                                viewModel.addVariant(
                                                    product.id,
                                                    updated.size,
                                                    colorName ?: "",
                                                    updated.stock
                                                )
                                            }
                                        }

                                        // 🔥 DELETE
                                        originalVariants.forEach { old ->

                                            val stillExists = editedVariants.any { it.id == old.id }

                                            if (!stillExists && old.color == colorName && old.id.isNotEmpty()) {
                                                viewModel.deleteVariant(old.id)
                                            }
                                        }

                                        // 🔥 FINAL REFRESH (VERY IMPORTANT)
                                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                            viewModel.fetchProductDetails(product.id)
                                        }
                                    }
                            }
                        )
                    }
                }
            }
        )
    }
}