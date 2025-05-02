package ru.vmestego.bll.services.invitations

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.bll.services.users.models.UserResponse

class InvitationsService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3

    suspend fun getPendingInvitations(token: String): List<InvitationResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events-invitations/pending") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
            return listOf()
        }

        return response.body<List<InvitationResponse>>()
    }

    suspend fun getSentInvitations(token: String): List<InvitationResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events-invitations/sent") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
            return listOf()
        }

        return response.body<List<InvitationResponse>>()
    }

    suspend fun acceptInvitation(token: String, inviteId: Long) {
        try {
            client.post("http://10.0.2.2:8080/events-invitations/${inviteId}/accept") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun rejectInvitation(token: String, inviteId: Long) {
        try {
            client.post("http://10.0.2.2:8080/events-invitations/${inviteId}/reject") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun revokeInvitation(token: String, inviteId: Long) {
        try {
            client.delete("http://10.0.2.2:8080/events-invitations/${inviteId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }
}

@Serializable
data class InvitationResponse(
    val id: Long,
    val event: EventResponse,
    val sender: UserResponse,
    val receiver: UserResponse,
    val status: EventInvitationStatus
)

@Serializable
enum class EventInvitationStatus {
    Pending,
    Accepted,
    Rejected
}