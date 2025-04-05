package ru.vmestego.bll.services.friends

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.friends.models.FriendRequestsResponse

class FriendsService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun acceptFriendRequest(requestId: Long) {
        val response: HttpResponse = client.post("http://10.0.2.2:8080/requests/${requestId}/accept") {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun rejectFriendRequest(requestId: Long) {
        val response: HttpResponse = client.post("http://10.0.2.2:8080/requests/${requestId}/reject") {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun cancelFriendRequest(requestId: Long) {
        val response: HttpResponse =
            client.delete("http://10.0.2.2:8080/requests/${requestId}") {
                contentType(ContentType.Application.Json)
            }
    }

    suspend fun getSentFriendRequests(): FriendRequestsResponse {
        val response: HttpResponse = client.get("http://10.0.2.2:8080/requests/sent") {
            contentType(ContentType.Application.Json)
        }

        return response.body<FriendRequestsResponse>()
    }
}