package ru.vmestego.ui.authActivity

import android.app.Application
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.auth.AuthService
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.ui.authActivity.models.LoginRequest
import ru.vmestego.ui.authActivity.models.LoginResponse
import ru.vmestego.data.SecureStorage
import ru.vmestego.utils.PasswordValidator
import java.util.zip.DataFormatException

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var login by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    val loginHasErrors by derivedStateOf {
        login.isEmpty()
    }

    val passwordHasErrors by derivedStateOf {
        !PasswordValidator.isValidPassword(password)
    }

    val hasValidationErrors by derivedStateOf {
        loginHasErrors && passwordHasErrors
    }

    var isLoading by mutableStateOf(false)
        private set

    var authorizeError by mutableStateOf("")
        private set

    private val secureStorage = SecureStorage.getStorageInstance(application)

    private val authService = AuthService()

    fun updateLogin(newLogin: String) {
        login = newLogin
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun authorizeUser(successCallback: () -> Unit) {
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