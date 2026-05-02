package com.example.hyperlocalecom.data.remote

import com.example.hyperlocalecom.data.local.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    //    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl("http://192.168.1.11:8000/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor {
                    TokenManager.getToken() // ⚠️ YOUR TOKEN SOURCE
                }
            )
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
//            .baseUrl("https://urbanrack.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }
}