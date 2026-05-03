package com.example.hyperlocalecom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.ui.screen.AddProductScreen
import com.example.hyperlocalecom.ui.screen.HomeScreen
import com.example.hyperlocalecom.ui.screen.LoginScreen
import com.example.hyperlocalecom.ui.screen.ProductDetailScreen
import com.example.hyperlocalecom.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    val token = TokenManager.getToken()

    val startDestination = if (token.isNullOrEmpty()) {
        "login"
    } else {
        "home"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(navController, viewModel)
        }

        composable("home") {
            HomeScreen(
//                email = "Shop Owner",
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->

            val productId = backStackEntry.arguments?.getString("productId")
                ?: throw IllegalArgumentException("Product ID missing")

            ProductDetailScreen(
                productId = productId,
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = "addProduct",
        ){
            AddProductScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = "addVariant/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            com.example.hyperlocalecom.ui.screen.AddVariantScreen(
                navController = navController,
                viewModel = viewModel,
                productId = productId
            )
        }
    }
}