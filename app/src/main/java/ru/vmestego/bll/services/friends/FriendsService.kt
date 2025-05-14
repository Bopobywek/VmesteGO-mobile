package ru.vmestego.bll.services.friends

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
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
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.friends.models.CreateInvitationRequest
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

    private val retryNumber = 3

    suspend fun getAllFriends(token: String): List<FriendResponse> {
        try {
            val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }

            return response.body<List<FriendResponse>>()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun acceptFriendRequest(token: String, requestId: Long) {
        try {
            client.post("${API_BASE_ADDRESS}/friends/requests/${requestId}/accept") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun rejectFriendRequest(token: String, requestId: Long) {
        try {
            client.post("${API_BASE_ADDRESS}/friends/requests/${requestId}/reject") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun cancelFriendRequest(token: String, requestId: Long) {
        try {
            client.delete("${API_BASE_ADDRESS}/friends/requests/${requestId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun removeFriend(token: String, userId: Long) {
        try {
            client.delete("${API_BASE_ADDRESS}/friends/${userId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getFriendRequest(token: String, fromUserId: String, toUserId: String): FriendRequestResponse? {
        try {
            val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/requests/users") {
                contentType(ContentType.Application.Json)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
                parameter("fromUserId", fromUserId)
                parameter("toUserId", toUserId)
                bearerAuth(token)
            }

            if (response.status == HttpStatusCode.BadRequest) {
                return null
            }

            return response.body<FriendRequestResponse>()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun createFriendRequest(token: String, userId: String) {
        try {
            val request = SendRequestForUserRequest(userId.toLong())
            client.post("${API_BASE_ADDRESS}/friends/requests") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(request)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getSentFriendRequests(token: String): List<FriendRequestResponse> {
        try {
            val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/requests/sent") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }

            return response.body<List<FriendRequestResponse>>()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun getIncomingFriendRequests(token: String): List<FriendRequestResponse> {
        try {
            val response: HttpResponse =
                client.get("${API_BASE_ADDRESS}/friends/requests/pending") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                }

            return response.body<List<FriendRequestResponse>>()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun getFriendsEvents(token: String): List<FriendsEventResponse> {
        try {
            val response: HttpResponse = client.get("${API_BASE_ADDRESS}/friends/events") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }

            return response.body<List<FriendsEventResponse>>()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun getFriendsStatusesForEvent(token: String, eventId: String): List<FriendStatusResponse> {
        try {
            val response: HttpResponse =
                client.get("${API_BASE_ADDRESS}/friends/events/${eventId}") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                }

            return response.body<List<FriendStatusResponse>>()
        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun inviteFriendOnEvent(token: String, eventId: Long, userId: Long) {
        try {
            var request = CreateInvitationRequest(eventId, userId)
            client.post("${API_BASE_ADDRESS}/events-invitations/invite") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(request)
            }
        } catch (_: Exception) {
        }
    }
}

