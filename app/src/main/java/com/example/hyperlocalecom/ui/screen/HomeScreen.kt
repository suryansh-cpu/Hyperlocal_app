package com.example.hyperlocalecom.ui.screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hyperlocalecom.viewmodel.AuthViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.hyperlocalecom.data.local.TokenManager

//import com.example.hyperlocalecom.data.local.TokenManager

@Composable
fun HomeScreen(email: String, viewModel: AuthViewModel = viewModel(),navController: NavController) {

//    val context = LocalContext.current
//    val tokenManager = com.example.hyperlocalecom.data.local.TokenManager(context)
//
//    val token by tokenManager.getToken.collectAsState(initial = null)
    val token = TokenManager.getToken()

    LaunchedEffect(token) {
        token?.let {
            viewModel.fetchStore(it)
            viewModel.fetchProducts(it)
        }
    }

    val store = viewModel.storeData.value

//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Welcome 🎉")
//        Text(email)
//
//        store?.let {
//            Text("Store Name: ${it.name}")
//            Text("Address: ${it.address}")
//            Text("Commission: ${it.commission_rate}%")
//        }
//    }
    val products = viewModel.products.value
//
//    LazyColumn {
//        items(products) { product ->
//            Text("Product: ${product.name}")
//            Text("Price: ₹${product.price}")
//        }
//    }
    LazyColumn {
        item {
            Text("Welcome 🎉")
            Text(email)
            store?.let {
                Text("Store Name: ${it.name}")
            }
        }

//        items(products) { product ->
//            Text("Product: ${product.name}")
//            Text("Price: ₹${product.price}")
//        }
        items(products) { product ->

            Text(
                text = "Product: ${product.name}",
                modifier = Modifier.clickable {
                    navController.navigate("productDetail/${product.id}")
                }
            )
        }
    }
}