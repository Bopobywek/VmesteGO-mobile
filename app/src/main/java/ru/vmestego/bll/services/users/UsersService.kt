package ru.vmestego.bll.services.users

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.users.models.UsersSearchResponse

class UsersService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getAllFriends(): UsersSearchResponse {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/friends") {
            contentType(ContentType.Application.Json)
        }

        return response.body<UsersSearchResponse>();
    }

    suspend fun findUsers(query: String): UsersSearchResponse {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/users/search") {
            url {
                parameters["q"] = query
            }

            contentType(ContentType.Application.Json)
        }

        return response.body<UsersSearchResponse>();
    }
}