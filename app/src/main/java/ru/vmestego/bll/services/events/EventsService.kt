package ru.vmestego.bll.services.events

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.bll.services.notifications.models.NotificationResponse
import ru.vmestego.bll.services.notifications.models.NotificationsResponse
import ru.vmestego.core.EventStatus

class EventsService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3

    suspend fun getEventsByStatus(userId: String?, eventStatus: EventStatus?) : List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
                parameter("userId", userId)
                parameter("eventStatus", eventStatus)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }

    suspend fun changeEventStatus(token: String, eventId: Long, eventStatus: EventStatus?) {
        val response: HttpResponse
        try {
            response = client.post("http://10.0.2.2:8080/events/${eventId}/status") {
                contentType(ContentType.Application.Json)
                setBody(eventStatus)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getEventById(token: String, eventId: Long) : EventResponse? {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/${eventId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return null
        }

        return response.body<EventResponse>()
    }
}