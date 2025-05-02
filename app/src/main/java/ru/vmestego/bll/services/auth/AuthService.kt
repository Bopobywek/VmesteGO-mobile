package ru.vmestego.bll.services.auth

import android.R
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.ui.authActivity.models.LoginRequest
import ru.vmestego.ui.authActivity.models.LoginResponse
import ru.vmestego.ui.authActivity.models.RegisterRequest
import ru.vmestego.ui.authActivity.models.RegisterResponse

class AuthService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    suspend fun authorizeUser(loginRequest: LoginRequest): LoginResponse {
        val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }

        return response.body<LoginResponse>();
    }

    suspend fun registerUser(registerRequest: RegisterRequest): RegisterResponse {
        val response: HttpResponse = client.post("http://10.0.2.2:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }

        return response.body<RegisterResponse>();
    }
}