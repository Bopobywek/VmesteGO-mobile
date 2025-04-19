package ru.vmestego.utils

import android.app.Application
import ru.vmestego.data.SecureStorage

class TokenDataProvider(application: Application) {
    private val secureStorage = SecureStorage.getStorageInstance(application)

    fun getToken() : String? {
        return secureStorage.getToken()
    }

    fun getUserIdFromToken(): String? {
        val token = secureStorage.getToken()

        if (token == null) {
            return null
        }

        val userId = JwtUtil.getUserIdFromToken(token)
        return userId;
    }

    fun removeToken() {
        secureStorage.removeToken()
    }
}