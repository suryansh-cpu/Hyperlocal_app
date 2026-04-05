//package com.example.hyperlocalecom.ui.screen
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.hyperlocalecom.data.local.TokenManager
//import com.example.hyperlocalecom.viewmodel.AuthViewModel
//
//@Composable
//fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
//
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
////    val context = LocalContext.current
////    val tokenManager = com.example.hyperlocalecom.data.local.TokenManager(context)
////    viewModel.tokenManager = com.example.hyperlocalecom.data.local.TokenManager(context)
////    TokenManager.saveToken(response.access_token)
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        Text("Shop Owner Login", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                viewModel.login(email, password)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Login")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (viewModel.isLoading.value) {
//            CircularProgressIndicator()
//        }
//
//        viewModel.loginState.value?.let {
//            LaunchedEffect(it) {
//                navController.navigate("home/${it.user.email}") {
//                    popUpTo("login") { inclusive = true }
//                }
//            }
//        }
//
//        viewModel.error.value?.let {
//            Text("❌ Error: $it")
//        }
//    }
//}

package com.example.hyperlocalecom.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hyperlocalecom.data.local.TokenManager
import com.example.hyperlocalecom.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Shop Owner Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(
                    email,
                    password,
                    onSuccess = { response ->

                        // ✅ SAVE TOKEN HERE
                        TokenManager.saveToken(response.access_token)

                        // ✅ NAVIGATE
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onError = {
                        errorMessage = it
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}