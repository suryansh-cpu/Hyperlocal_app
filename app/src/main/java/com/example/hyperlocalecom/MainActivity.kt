package com.example.hyperlocalecom
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.ui.navigation.AppNavigation
import com.example.hyperlocalecom.ui.screen.HomeScreen
import com.example.hyperlocalecom.ui.screen.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        TokenManager.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContent {
//            val navController = rememberNavController()
//
//            NavHost(navController = navController, startDestination = "login") {
//
//                composable("login") {
//                    LoginScreen(navController)
//                }
//
//                composable("home/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email") ?: ""
//                    HomeScreen(email)
//                }
//            }
//        }
        setContent {
            AppNavigation()
        }
    }
}