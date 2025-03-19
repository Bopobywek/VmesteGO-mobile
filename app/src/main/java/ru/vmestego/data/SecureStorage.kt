package ru.vmestego.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey(context, "vmesteGo"),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        with(sharedPreferences.edit()) {
            putString("bearer_token", token)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("bearer_token", null)
    }

    fun removeToken() {
        with(sharedPreferences.edit()) {
            remove("bearer_token")
            apply()
        }
    }

    companion object {
        @Volatile
        private var Instance: SecureStorage? = null

        fun getStorageInstance(context: Context): SecureStorage {
            return Instance ?: synchronized(this) {
                SecureStorage(context)
            }
        }
    }
}