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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.bll.services.notifications.models.NotificationResponse
import ru.vmestego.bll.services.notifications.models.NotificationsResponse
import ru.vmestego.bll.services.shared.models.CategoryResponse
import ru.vmestego.core.EventStatus
import ru.vmestego.ui.ticketActivity.EventDto
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

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

    suspend fun getEventsByStatus(token: String, userId: String?, eventStatus: EventStatus?) : List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
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

    suspend fun getAllAvailableCategories(token: String): List<CategoryResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/categories") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return listOf()
        }

        return response.body<List<CategoryResponse>>()
    }

    suspend fun createEvent(token: String, event: CreateEventRequest): EventResponse? {
        val response: HttpResponse
        try {
            response = client.post("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
                setBody(event)
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

@Serializable
data class CreateEventRequest(
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class) val dates: LocalDateTime,
    val location: String,
    val description: String,
    val ageRestriction: Int,
    val price: Double,
    val isPrivate: Boolean,
    val eventCategoryNames: List<String> = emptyList(),
    val eventImages: List<String> = emptyList(),
    val externalId: Int? = null
)
