package ru.vmestego.auth


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
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.vmestego.auth.models.RegisterRequest
import ru.vmestego.auth.models.RegisterResponse
import ru.vmestego.data.SecureStorage
import ru.vmestego.utils.PasswordValidator

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    var login by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val emailHasFormatError by derivedStateOf {
        email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val emailHasErrors by derivedStateOf {
        emailHasFormatError
    }
    var emailError by mutableStateOf("")
        private set

    val loginHasErrors by derivedStateOf {
        login.isEmpty()
    }
    var loginError by mutableStateOf("")
        private set

    val passwordHasErrors by derivedStateOf {
        !PasswordValidator.isValidPassword(password)
    }
    var passwordError by mutableStateOf("")
        private set

    val hasValidationErrors by derivedStateOf {
        loginHasErrors && emailHasErrors && passwordHasErrors
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

    fun updateLogin(newLogin: String) {
        login = newLogin
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun registerUser(successCallback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            val registerRequest = RegisterRequest(
                login = login,
                email = email,
                password = password
            )
            try {
//                https://stackoverflow.com/questions/5528850/how-do-you-connect-localhost-in-the-android-emulator
                val response: HttpResponse = client.post("http://10.0.2.2:8080/register") {
                    contentType(ContentType.Application.Json)
                    setBody(registerRequest)
                }

                val responseBody: RegisterResponse = response.body()
                Log.i("Token", responseBody.token)
                secureStorage.saveToken(responseBody.token)
                Log.e("Registartion", "Registration response: ${response.status}")
            } catch (e: Exception) {
                Log.e("Registartion","Registration failed: ${e.message}")
            } finally {
                isLoading = false
            }

            withContext(Dispatchers.Main) {
                successCallback()
            }
        }
    }
}