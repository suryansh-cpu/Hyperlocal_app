//package com.example.hyperlocalecom.data.local
//
//import android.content.Context
//import androidx.datastore.preferences.core.*
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
//
//class TokenManager(private val context: Context) {
//
//    companion object {
//        val TOKEN_KEY = stringPreferencesKey("auth_token")
//    }
//
//    suspend fun saveToken(token: String) {
//        context.dataStore.edit { prefs ->
//            prefs[TOKEN_KEY] = token
//        }
//    }
//
//    val getToken: Flow<String?> = context.dataStore.data
//        .map { prefs ->
//            prefs[TOKEN_KEY]
//        }
//}
package com.example.hyperlocalecom.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object TokenManager {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit { clear() }
    }
}