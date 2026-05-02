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
import com.example.hyperlocalecom.data.model.VariantRequest
import com.example.hyperlocalecom.data.remote.RetrofitInstance.productApi
import com.example.hyperlocalecom.data.repository.uploadImageToCloudinary
import com.example.hyperlocalecom.ui.components.CustomTextField
import com.example.hyperlocalecom.ui.components.VariantCard
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {

    var productData by remember { mutableStateOf(ProductData()) }
    var variants by remember { mutableStateOf(mutableListOf<VariantRequest>()) }
    var uploadedImages by remember { mutableStateOf<List<CloudinaryResponse>?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var newSize by remember { mutableStateOf("") }
    var newStock by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    // Image Picker
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
                        scope.launch {

                            uploading = true

                            // 1. Upload images if not uploaded
                            if (uploadedImages == null && productData.images.isNotEmpty()) {

                                val results = mutableListOf<CloudinaryResponse>()

                                for (uri in productData.images) {
                                    val uploaded = uploadImageToCloudinary(context, uri)
                                    if (uploaded != null) {
                                        results.add(uploaded)
                                    }
                                }

                                uploadedImages = results
                            }

                            // 2. CALL BACKEND APIs

                            try {

                                // 🔹 CREATE PRODUCT
                                val product = productApi.createProduct(
                                    ProductCreateRequest(
                                        name = productData.name,
                                        brand = productData.brand,
                                        description = productData.description,
                                        cloth_material = productData.material
                                    )
                                )

                                val productId = product.id

                                // 🔹 ADD VARIANTS
//                                productData.size.forEach { (size, stock) ->
//
//                                    productApi.addVariant(
//                                        productId,
//                                        VariantRequest(
//                                            size = size,
//                                            color = productData.color,
//                                            price = productData.price.toDoubleOrNull() ?: 0.0,
//                                            stock_qty = stock
//                                        )
//                                    )
//                                }
                                productData.size.forEach { (size, stock) ->
                                    viewModel.addVariant(
                                        productId = productId,
                                        size = size,
                                        color = productData.color,
                                        stock = stock
                                    )
                                }

                                // 🔹 ADD IMAGES
                                uploadedImages?.forEachIndexed { index, img ->
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

                                Toast.makeText(context, "Product Added", Toast.LENGTH_SHORT).show()

                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG)
                                    .show()
                            }

                            uploading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uploading) "Uploading..." else "Add Product")
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate("variant")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Add Variant")
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

            // 🔥 MEDIA
            item {
                Text("Media", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                //          ADD IMAGE BUTTON

                Button(onClick = { imageLauncher.launch("image/*") }) {
                    Text("Upload Images")
                }

                Spacer(Modifier.height(8.dp))

                Button(onClick = {
                    val uri = createImageUri(context)
                    cameraImageUri.value = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Capture Image")
                }
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    items(productData.images.size) { index ->
//                        Image(
//                            painter = rememberAsyncImagePainter(productData.images[index]),
//                            contentDescription = null,
//                            contentScale = ContentScale.FillWidth,
//                            modifier = Modifier
//                                .size(200.dp)
//                                .clickable {
//                                    productData = productData.copy(coverImageIndex = index)
//                                }
//                        )
//                    }
//                }
                LazyRow {
                    items(productData.images.size) { index ->

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .padding(4.dp)
                        ) {

                            // 🔹 IMAGE
                            Image(
                                painter = rememberAsyncImagePainter(productData.images[index]),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable {
                                        productData = productData.copy(
                                            coverImageIndex = index
                                        )
                                    }
                            )

                            // 🔹 REMOVE BUTTON (❌)
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Remove",
//                                tint = Color.White,
//                                modifier = Modifier
//                                    .align(Alignment.TopEnd)
//                                    .size(25.dp)
//                                    .clickable {
//
//                                        val newList = productData.images.toMutableList()
//                                        newList.removeAt(index)
//
//                                        productData = productData.copy(
//                                            images = newList,
//
//                                            // adjust cover index safely
//                                            coverImageIndex = when {
//                                                productData.coverImageIndex == index -> 0
//                                                productData.coverImageIndex > index -> productData.coverImageIndex - 1
//                                                else -> productData.coverImageIndex
//                                            }
//                                        )
//                                    }
//                            )
                            IconButton(
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color.Black
                                ),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(25.dp),
                                onClick = {
                                    val newList = productData.images.toMutableList()
                                    newList.removeAt(index)

                                    productData = productData.copy(
                                        images = newList,

                                        // adjust cover index safely
                                        coverImageIndex = when {
                                            productData.coverImageIndex == index -> 0
                                            productData.coverImageIndex > index -> productData.coverImageIndex - 1
                                            else -> productData.coverImageIndex
                                        }
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 🔥 PRODUCT DETAILS
            item {
                SectionTitle("Product Details")

                CustomTextField("Product Name", productData.name) {
                    productData = productData.copy(name = it)
                }

                CustomTextField("Brand", productData.brand) {
                    productData = productData.copy(brand = it)
                }

                CustomTextField("Material", productData.material) {
                    productData = productData.copy(material = it)
                }

                CustomTextField("Description", productData.description, multiLine = true) {
                    productData = productData.copy(description = it)
                }
            }

            // 🔥 ATTRIBUTES
            item {
                SectionTitle("Attributes")

                Row {
                    CustomTextField(
                        label = "Color",
                        value = productData.color
                    ) {
                        productData = productData.copy(color = it)
                    }

                    Spacer(Modifier.width(8.dp))

//                    CustomTextField(
//                        label = "Size",
//                        value = productData.size
//                    ) {
//                        productData = productData.copy(size = it)
//                    }
                }
            }

            // 🔥 PRICE
            item {
                SectionTitle("Pricing")

                CustomTextField("Price", productData.price) {
                    productData = productData.copy(price = it)
                }
            }

            item {
                SectionTitle("Sizes")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    for (size in productData.size.keys) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$size : ${productData.size[size]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = {
                                val updatedMap = productData.size.toMutableMap()
                                updatedMap.remove(size)

                                productData = productData.copy(size = updatedMap)
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Row(
                        modifier = Modifier.weight(1f)
                    ){
                        CustomTextField(
                            label = "Size",
                            value = newSize
                        ) {
                            newSize = it.uppercase()
                        }
                    }

                    Row(
                        modifier = Modifier.weight(1f)
                    ){
                        CustomTextField(
                            label = "Stock",
                            value = newStock
                        ) {
                            newStock = it.uppercase()
                        }
                    }
                }
                Text(
                    "+ Add",
                    modifier = Modifier
                        .clickable {
                            if (newSize.isNotBlank() && !productData.size.containsKey(newSize) && newStock.isNotBlank()) {

                                val updatedMap = productData.size.toMutableMap()
                                updatedMap[newSize] = newStock.toInt()

                                productData = productData.copy(size = updatedMap)

                                newSize = ""
                                newStock = ""
                            } else if (!newSize.isNotBlank()) {
                                Toast.makeText(context, "Please enter a size", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (productData.size.containsKey(newSize)) {
                                Toast.makeText(context, "Size already exists", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (newStock.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Please enter stock quantity for the size",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

//            // 🔥 VARIANTS PREVIEW
//            if (variants.isNotEmpty()) {
//                item {
//                    SectionTitle("Variants")
//
//                    variants.forEach {
//                        VariantCard(it, onDecrease = {}, onIncrease = {})
//                    }
//                }
//            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

fun createImageUri(context: Context): Uri {
    val file = File(
        context.cacheDir,
        "camera_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}