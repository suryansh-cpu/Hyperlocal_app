package com.example.hyperlocalecom.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import com.example.hyperlocalecom.ui.components.VariantCard

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

//        // 🔥 RIGHT - VARIANTS
//        Column(
//            modifier = Modifier
//                .weight(1.5f)
//                .padding(16.dp)
//        ) {
//
//            Text(
//                text = product.name,
//                style = MaterialTheme.typography.headlineSmall
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            val grouped = data.variants.groupBy { it.color }
//
//            LazyColumn {
//
//                grouped.forEach { (color, list) ->
//
//                    item {
//                        Text(
//                            text = "Color: $color",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                    }
//
//                    items(list) { variant ->
//                        VariantCard(
//                            variant = variant,
//                            onIncrease = {},
//                            onDecrease = {},
//                            onEdit = {}
//                        )
//                    }
//
//                    item {
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
//            }
//        }
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
//            val grouped = data.variants.groupBy { it.color }
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
                        Text(
                            text = "Color: $color",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(list) { variant ->

//                        VariantCard(
//                            variant = variant,
//
//                            onIncrease = { v ->
//                                val index = variantsState.indexOf(v)
//                                if (index != -1) {
//                                    variantsState[index] =
//                                        variantsState[index].copy(stock = v.stock + 1)
//                                }
//                            },
//
//                            onDecrease = { v ->
//                                val index = variantsState.indexOf(v)
//                                if (index != -1 && v.stock > 0) {
//                                    variantsState[index] =
//                                        variantsState[index].copy(stock = v.stock - 1)
//                                }
//                            },
//
//                            onEdit = {}
//                        )
                        VariantCard(
                            variant = variant,

//                            onIncrease = { v ->
//                                val index = variantsState.indexOf(v)
//                                if (index != -1) {
//                                    val newStock = v.stock + 1
//
//                                    variantsState[index] =
//                                        variantsState[index].copy(stock = newStock)
//
//                                    viewModel.updateVariantStock(v.id, newStock) // 🔥 API CALL
//                                }
//                            },
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

                            onEdit = {}
                        )
                    }
                }
            }
//            LazyColumn {
//
//                grouped.forEach { (color, list) ->
//
//                    item {
//                        Text(
//                            text = "Color: $color",
//                            style = MaterialTheme.typography.titleLarge
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//
//                    items(list) { variant ->
//                        VariantCard(
//                            variant = variant,
//                            onIncrease = { v ->
//                                v.stock += 1
//                            },
//                            onDecrease = { v ->
//                                if (v.stock > 0) v.stock -= 1
//                            },
//                            onEdit = {}
//                        )
//                    }
//
//                    item {
//                        Spacer(modifier = Modifier.height(20.dp))
//                    }
//                }
//            }
        }
    }
}