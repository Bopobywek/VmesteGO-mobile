package ru.vmestego

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.vmestego.data.SecureStorage

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val secureStorage = SecureStorage.getStorageInstance(application)

    fun logout() {
        secureStorage.removeToken()
    }
}