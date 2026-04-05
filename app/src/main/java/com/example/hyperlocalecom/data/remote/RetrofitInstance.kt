package com.example.hyperlocalecom.data.remote
//import com.example.hyperlocalecom.data.remote.RetrofitInstance.BASE_URL
//import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//object RetrofitInstance {
//
//    private const val BASE_URL = "http://192.168.1.5:8000/"
//
//    val api: AuthApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthApi::class.java)
//    }
//}

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.5:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
//
//object RetrofitInstance {
//
//    private const val BASE_URL = "http://192.168.1.5:8000/"
//
//    fun create(token: String?): AuthApi {
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val requestBuilder = chain.request().newBuilder()
//
//                if (token != null) {
//                    requestBuilder.addHeader(
//                        "Authorization",
//                        "Bearer $token"
//                    )
//                }
//
//                chain.proceed(requestBuilder.build())
//            }
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthApi::class.java)
//    }
//}