package ru.vmestego.bll.services.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.exceptions.HttpServiceException
import ru.vmestego.core.API_BASE_ADDRESS
import ru.vmestego.ui.authActivity.models.LoginRequest
import ru.vmestego.ui.authActivity.models.LoginResponse
import ru.vmestego.ui.authActivity.models.RegisterRequest
import ru.vmestego.ui.authActivity.models.RegisterResponse

class AuthService {
    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun authorizeUser(loginRequest: LoginRequest): LoginResponse {
        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }

            if (response.status.isSuccess()) {
                return response.body<LoginResponse>()
            }

        } catch (_: Exception) {
            throw HttpServiceException(null, "Unhandled error")
        }

        throw HttpServiceException(response.status, response.body())
    }

    suspend fun registerUser(registerRequest: RegisterRequest): RegisterResponse {
        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(registerRequest)
            }

            if (response.status.isSuccess()) {
                return response.body<RegisterResponse>()
            }
        } catch (_: Exception) {
            throw HttpServiceException(null, "Unhandled error")
        }

        throw HttpServiceException(response.status, response.body())
    }
}