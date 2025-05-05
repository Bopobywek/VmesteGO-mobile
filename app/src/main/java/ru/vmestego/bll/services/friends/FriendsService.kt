package ru.vmestego.bll.services.friends

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.friends.models.FriendRequestResponse
import ru.vmestego.bll.services.friends.models.FriendResponse
import ru.vmestego.bll.services.friends.models.FriendStatusResponse
import ru.vmestego.bll.services.friends.models.FriendsEventResponse
import ru.vmestego.bll.services.friends.models.SendRequestForUserRequest
import ru.vmestego.core.API_BASE_ADDRESS

class FriendsService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getAllFriends(token: String): List<FriendResponse> {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<List<FriendResponse>>()
    }

    suspend fun acceptFriendRequest(token: String, requestId: Long) {
        client.post("${API_BASE_ADDRESS}/friends/requests/${requestId}/accept") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }

    suspend fun rejectFriendRequest(token: String, requestId: Long) {
        client.post("${API_BASE_ADDRESS}/friends/requests/${requestId}/reject") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }

    suspend fun cancelFriendRequest(token: String, requestId: Long) {
        client.delete("${API_BASE_ADDRESS}/friends/requests/${requestId}") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }

    suspend fun removeFriend(token: String, userId: Long) {
        client.post("${API_BASE_ADDRESS}/friends/${userId}") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }

    suspend fun getFriendRequest(token: String, fromUserId: String, toUserId: String): FriendRequestResponse? {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/requests/users") {
            contentType(ContentType.Application.Json)
            parameter("fromUserId", fromUserId)
            parameter("toUserId", toUserId)
            bearerAuth(token)
        }

        if (response.status == HttpStatusCode.BadRequest) {
            return null
        }

        return response.body<FriendRequestResponse>()
    }

    suspend fun createFriendRequest(token: String, userId: String) {
        val request = SendRequestForUserRequest(userId.toLong())
        client.post("${API_BASE_ADDRESS}/friends/requests") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }
    }

    suspend fun getSentFriendRequests(token: String): List<FriendRequestResponse> {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/requests/sent") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<List<FriendRequestResponse>>()
    }

    suspend fun getIncomingFriendRequests(token: String): List<FriendRequestResponse> {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/requests/pending") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<List<FriendRequestResponse>>()
    }

    suspend fun getFriendsEvents(token: String): List<FriendsEventResponse> {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/events") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<List<FriendsEventResponse>>()
    }

    suspend fun getFriendsStatusesForEvent(token: String, eventId: String): List<FriendStatusResponse> {
        val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/events/${eventId}") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }

        return response.body<List<FriendStatusResponse>>()
    }

    suspend fun inviteFriendOnEvent(token: String, eventId: Long, userId: Long) {
        var request = CreateInvitationRequest(eventId, userId)
        client.post("${API_BASE_ADDRESS}/events-invitations/invite") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }
    }
}

@Serializable
data class CreateInvitationRequest(
    val eventId: Long,
    val receiverId: Long)
