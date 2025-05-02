package ru.vmestego.bll.services.users

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.users.models.ConfirmImageUploadRequest
import ru.vmestego.bll.services.users.models.GetUploadImageUrlResponse
import ru.vmestego.bll.services.users.models.UserResponse

class UsersService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getUploadImageUrl(userId: String, token: String): GetUploadImageUrlResponse {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/users/${userId}/images-upload") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<GetUploadImageUrlResponse>();
    }

    suspend fun putImage(signedUrl: String, imageBytes: ByteArray) {
        val response: HttpResponse = client.put(signedUrl) {
            contentType(ContentType.Image.JPEG)
            setBody(imageBytes)
        }
    }

    suspend fun confirmImageUpload(userId: String, token: String, key: String): UserResponse {
        val response: HttpResponse = client.post("http://10.0.2.2:8080/users/${userId}/confirm-image-upload") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(ConfirmImageUploadRequest(key))
        }

        return response.body<UserResponse>();
    }

    suspend fun findUsers(token: String, query: String): List<UserResponse> {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/users/search") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            parameter("username", query)
            parameter("page", "1")
            parameter("pageSize", "50")
        }

        return response.body<List<UserResponse>>();
    }

    suspend fun getUserInfoById(userId: String, token: String): UserResponse {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/users/${userId}") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<UserResponse>();
    }
}

