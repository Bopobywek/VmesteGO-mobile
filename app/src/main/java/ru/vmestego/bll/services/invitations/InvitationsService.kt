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
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.invitations.models.InvitationResponse
import ru.vmestego.core.API_BASE_ADDRESS

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
            response = client.get("${API_BASE_ADDRESS}/events-invitations/pending") {
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
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
            response = client.get("${API_BASE_ADDRESS}/events-invitations/sent") {
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
            return response.body<List<InvitationResponse>>()

        } catch (_: Exception) {
            return emptyList()
        }
    }

    suspend fun acceptInvitation(token: String, inviteId: Long) {
        try {
            client.post("${API_BASE_ADDRESS}/events-invitations/${inviteId}/accept") {
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun rejectInvitation(token: String, inviteId: Long) {
        try {
            client.post("${API_BASE_ADDRESS}/events-invitations/${inviteId}/reject") {
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun revokeInvitation(token: String, inviteId: Long) {
        try {
            client.delete("${API_BASE_ADDRESS}/events-invitations/${inviteId}") {
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
                contentType(ContentType.Application.Json)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }
}

