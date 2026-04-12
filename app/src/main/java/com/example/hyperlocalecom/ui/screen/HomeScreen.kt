package com.example.hyperlocalecom.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors

//import com.example.hyperlocalecom.data.local.TokenManager

@Composable
fun HomeScreen(
//    email: String,
    viewModel: AuthViewModel = viewModel(), navController: NavController
) {
    val token = TokenManager.getToken()
    val store = viewModel.storeData.value
    val products = viewModel.products.value
    LaunchedEffect(token) {
        token?.let {
            viewModel.fetchStore(it)
            viewModel.fetchProducts(it)
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
        ){
            Text(
                text = "Welcome to ${store?.name}!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp).weight(1f),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
//                softWrap = false
            )
            HomeTopBarMenu(navController)
        }
        LazyColumn {
            items(products) { product ->

                Text(
                    text = "Product: ${product.name}", modifier = Modifier.clickable {
                        navController.navigate("productDetail/${product.id}")
                    })
            }
        }
    }
}
@Composable
fun HomeTopBarMenu(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }

    Box{
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu"
            )
        }

        // 📂 Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            // 👤 Profile
            DropdownMenuItem(
                text = { Text("Profile") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("profile")
                }
            )

            // 📜 History
            DropdownMenuItem(
                text = { Text("History") },
                leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("history")
                }
            )

            // 📤 Upload Bill
            DropdownMenuItem(
                text = { Text("Upload Bill") },
                leadingIcon = { Icon(Icons.Filled.AddCircle, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("upload_bill")
                }
            )

            // ➖ Divider (optional but looks better)
            HorizontalDivider()

            // 🚪 Logout
            DropdownMenuItem(
                text = { Text("Logout",color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Filled.Warning, contentDescription = null,tint = MaterialTheme.colorScheme.error) },
                onClick = {
                    expanded = false

                    // ✅ Your existing logout logic
                    TokenManager.clearToken()

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}