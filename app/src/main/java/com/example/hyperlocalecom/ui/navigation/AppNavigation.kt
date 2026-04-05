package com.example.hyperlocalecom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.hyperlocalecom.ui.screen.*
import com.example.hyperlocalecom.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("home") {
//            HomeScreen(email = "Shop Owner", viewModel)
            HomeScreen(
                email = "Shop Owner",
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            ProductDetailScreen(
                productId = productId,
                viewModel = viewModel
            )
        }
    }
}