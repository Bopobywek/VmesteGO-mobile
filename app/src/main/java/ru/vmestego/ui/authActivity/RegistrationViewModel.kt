package ru.vmestego.ui.authActivity


import android.app.Application
import android.util.Log
import android.widget.Toast
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
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.auth.AuthService
import ru.vmestego.ui.authActivity.models.RegisterRequest
import ru.vmestego.ui.authActivity.models.RegisterResponse
import ru.vmestego.data.SecureStorage
import ru.vmestego.utils.PasswordValidator

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val _application = application

    var isLoading by mutableStateOf(false)
        private set

    private val emailHasFormatError by derivedStateOf {
        email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    var login by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loginError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    var loginFocusedOnce by mutableStateOf(false)
    var emailFocusedOnce by mutableStateOf(false)
    var passwordFocusedOnce by mutableStateOf(false)

    val hasValidationErrors: Boolean
        get() = login.isBlank() || email.isBlank() || password.isBlank() || emailHasFormatError

    fun updateLogin(newLogin: String) {
        login = newLogin
        validateLogin()
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        validateEmail()
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        validatePassword()
    }

    fun validateLogin() {
        loginError = if (login.isBlank() && loginFocusedOnce) "Логин не может быть пустым" else null
    }

    fun validateEmail() {
        emailError = if (email.isBlank() && emailFocusedOnce) {
            "Email не может быть пустым"
        } else if (emailHasFormatError) {
            "Email должен соответствовать формату"
        } else {
            null
        }
    }

    fun validatePassword() {
        passwordError = if (password.isBlank() && passwordFocusedOnce) "Пароль не может быть пустым" else null
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val secureStorage = SecureStorage.getStorageInstance(application)
    private val authService = AuthService()

    fun registerUser(successCallback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val registerRequest = RegisterRequest(
                username = login,
                password = password
            )
            try {
//                https://stackoverflow.com/questions/5528850/how-do-you-connect-localhost-in-the-android-emulator

                val response = authService.registerUser(registerRequest)
                Log.i("Token", response.token)
                secureStorage.saveToken(response.token)

                withContext(Dispatchers.Main) {
                    Toast.makeText(_application, "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show()
                    successCallback()
                }
            } catch (e: Exception) {
                Log.e("Registartion","Registration failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(_application, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                    successCallback()
                }
            } finally {
                isLoading = false
            }
        }
    }
}