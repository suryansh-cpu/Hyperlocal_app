package com.example.hyperlocalecom.ui.screen

import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.data.model.Product
import com.example.hyperlocalecom.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    viewModel: AuthViewModel = viewModel(), navController: NavController
) {
    val token = TokenManager.getToken()
    val store = viewModel.storeData.value
    val products = viewModel.products.value
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(token) {
        if (token != null) {
            viewModel.fetchStore()
            viewModel.fetchProducts()
        }
    }

    LaunchedEffect(searchQuery) {
        val currentToken = TokenManager.getToken()
        if (currentToken != null) {
            kotlinx.coroutines.delay(200)
            if (searchQuery.isBlank()) {
                viewModel.fetchProducts(null)
            } else {
                viewModel.fetchProducts(searchQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome to ${store?.name ?: "Store"}!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
            )
            HomeTopBarMenu(navController)
        }
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search by name or brand...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        LazyColumn {
            items(products) { product ->
                ProductCard(
                    product = product,
                    navController = navController
                )
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ){
        FloatingActionButton(
            onClick = {
                navController.navigate("addProduct")
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Text("Add Product", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun HomeTopBarMenu(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Menu, contentDescription = "Menu"
            )
        }

        // 📂 Dropdown Menu
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {

            // 👤 Profile
            DropdownMenuItem(
                text = { Text("Profile") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("profile")
                })

            // 📜 History
            DropdownMenuItem(
                text = { Text("History") },
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("history")
                })

            // 📤 Upload Bill
            DropdownMenuItem(
                text = { Text("Upload Bill") },
                leadingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("upload_bill")
                })

            // ➖ Divider (optional but looks better)
            HorizontalDivider()

            // 🚪 Logout
            DropdownMenuItem(
                text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    expanded = false

                    // ✅ Your existing logout logic
                    TokenManager.clearToken()

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                })
        }
    }
}

@Composable
fun ProductCard(
    product: Product, navController: NavController, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate(
                    "productDetail/${Uri.encode(product.id)}"
                )
            }, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            // 🔹 IMAGE
            if (!product.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image")
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {

                // 🔸 NAME
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔸 BRAND
                Text(
                    text = "Brand: ${product.brand ?: "--"}", color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔸 STOCK
                Text(
                    text = "Stock: ${product.totalStock}",
                    color = if (product.totalStock > 0) Color(0xFF2E7D32) else Color.Red
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔸 VARIANTS
                Text(
                    text = "Variants: ${product.variantCount}", color = Color.Gray
                )
            }
        }
    }
}
