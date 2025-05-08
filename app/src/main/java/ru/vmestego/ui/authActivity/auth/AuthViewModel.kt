package ru.vmestego.ui.authActivity.auth

import android.app.Application
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.services.auth.AuthService
import ru.vmestego.ui.authActivity.models.LoginRequest
import ru.vmestego.data.SecureStorage
import ru.vmestego.utils.PasswordValidator

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var login by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var authorizeError by mutableStateOf("")
        private set

    private val secureStorage = SecureStorage.getStorageInstance(application)

    private val authService = AuthService()

    fun updateLogin(newLogin: String) {
        login = newLogin
        loginError = null
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        passwordError = null
    }

    private fun validateForm(): Boolean {
        var isValid = true

        loginError = if (login.isBlank()) {
            isValid = false
            "Логин должен быть заполнен"
        } else null

        passwordError = if (password.isBlank()) {
            isValid = false
            "Пароль должен быть заполнен"
        } else null

        return isValid
    }

    fun authorizeUser(successCallback: () -> Unit) {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            var isSuccess = false
            try {
                val loginRequest = LoginRequest(username = login, password = password)
                val response = authService.authorizeUser(loginRequest)
                isSuccess = true
                Log.i("Token", response.token)
                secureStorage.saveToken(response.token)
            } catch (e: Exception) {
                authorizeError = "Не удалось войти, попробуйте ещё раз"
                Log.e("Login","Registration failed: ${e.message}")
            } finally {
                isLoading = false
            }

            if (!isSuccess) {
                return@launch
            }

            withContext(Dispatchers.Main) {
                successCallback()
            }
        }
    }

    fun clearErrors() {
        authorizeError = ""
    }
}