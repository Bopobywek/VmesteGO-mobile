package ru.vmestego.bll.services.notifications

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.notifications.models.NotificationResponse
import ru.vmestego.bll.services.notifications.models.NotificationsResponse
import ru.vmestego.core.API_BASE_ADDRESS

class NotificationService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3;

    suspend fun getAllNotifications(token: String): NotificationsResponse {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/notifications") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return NotificationsResponse(listOf())
        }

        val list = response.body<List<NotificationResponse>>()
        return NotificationsResponse(list)
    }

    suspend fun markAsRead(token: String, id: Long) {
        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/notifications/${id}/read") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
        }
    }
}