//import androidx.compose.runtime.Composable
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.rememberNavController
//import com.example.hyperlocalecom.data.local.TokenManager
//import com.example.hyperlocalecom.ui.screen.HomeScreen
//import com.example.hyperlocalecom.ui.screen.LoginScreen
//import com.example.hyperlocalecom.ui.screen.ProductDetailScreen
//import com.example.hyperlocalecom.viewmodel.AuthViewModel

package com.example.hyperlocalecom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.ui.screen.*
import com.example.hyperlocalecom.viewmodel.AuthViewModel
//
//@Composable
//fun AppNavigation() {
//
//    val navController = rememberNavController()
//    val viewModel: AuthViewModel = viewModel()
//
//    NavHost(
//        navController = navController,
//        startDestination = "login"
//    ) {
//
//        composable("login") {
//            LoginScreen(navController, viewModel)
//        }
//
//        composable("home") {
////            HomeScreen(email = "Shop Owner", viewModel)
//            HomeScreen(
//                email = "Shop Owner",
//                viewModel = viewModel,
//                navController = navController
//            )
//        }
//
//        composable("productDetail/{productId}") { backStackEntry ->
//            val productId = backStackEntry.arguments?.getString("productId") ?: ""
//
//            ProductDetailScreen(
//                productId = productId,
//                viewModel = viewModel
//            )
//        }
//    }
//}
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