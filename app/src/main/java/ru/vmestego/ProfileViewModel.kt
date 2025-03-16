package ru.vmestego

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import ru.vmestego.auth.AuthActivity
import ru.vmestego.data.SecureStorage

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val secureStorage = SecureStorage.getStorageInstance(application)
    private val applicationContext = application

    fun logout() {
        secureStorage.removeToken()
    }
}