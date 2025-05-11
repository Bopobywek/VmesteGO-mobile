package ru.vmestego.bll.services.events

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
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.exceptions.HttpServiceException
import ru.vmestego.bll.services.events.models.ConfirmEventImageUploadRequest
import ru.vmestego.bll.services.events.models.CreateEventRequest
import ru.vmestego.bll.services.events.models.GetEventUploadImageUrlResponse
import ru.vmestego.bll.services.shared.models.CategoryResponse
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.core.API_BASE_ADDRESS
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

    suspend fun getEventsByStatus(
        token: String,
        userId: String?,
        eventStatus: EventStatus?
    ): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                parameter("userId", userId)
                parameter("eventStatus", eventStatus)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<EventResponse>>()
        } catch (_: Exception) {
            return listOf()
        }

    }

    suspend fun changeEventStatus(token: String, eventId: Long, eventStatus: EventStatus?) {
        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/events/${eventId}/status") {
                contentType(ContentType.Application.Json)
                setBody(eventStatus)
                bearerAuth(token)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getEventById(token: String, eventId: Long): EventResponse? {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/${eventId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<EventResponse>()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun getAllAvailableCategories(token: String): List<CategoryResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/categories") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<CategoryResponse>>()
        } catch (_: Exception) {
            return listOf()
        }
    }

    suspend fun deleteEvent(token: String, eventId: Long) {
        val response: HttpResponse
        try {
            response = client.delete("${API_BASE_ADDRESS}/events/${eventId}") {
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
        }
    }

    suspend fun createEvent(token: String, event: CreateEventRequest): EventResponse? {
        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/events") {
                contentType(ContentType.Application.Json)
                setBody(event)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<EventResponse>()

        } catch (_: Exception) {
            return null
        }
    }

    suspend fun updateEvent(token: String, eventId: Long, event: CreateEventRequest): EventResponse? {
        val response: HttpResponse
        try {
            response = client.put("${API_BASE_ADDRESS}/events/${eventId}") {
                contentType(ContentType.Application.Json)
                setBody(event)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<EventResponse>()

        } catch (_: Exception) {
            return null
        }
    }

    suspend fun getUploadImageUrl(eventId: Long, token: String): GetEventUploadImageUrlResponse {
        try {
            val response: HttpResponse =
                client.get("${API_BASE_ADDRESS}/events/${eventId}/images-upload/url") {
                    contentType(ContentType.Application.Json)
                    retry {
                        retryOnExceptionOrServerErrors(retryNumber)
                    }
                    bearerAuth(token)
                }

            return response.body<GetEventUploadImageUrlResponse>()
        } catch (_: Exception) {
            throw HttpServiceException(null, "Unhandled exception")
        }
    }

    suspend fun putImage(signedUrl: String, imageBytes: ByteArray) {
        try {
            client.put(signedUrl) {
                contentType(ContentType.Image.Any)
                setBody(imageBytes)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun confirmImageUpload(eventId: Long, token: String, key: String) {
        try {
            val response = client.post("${API_BASE_ADDRESS}/events/${eventId}/images-upload/confirm") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(ConfirmEventImageUploadRequest(key, 0))
            }

            response.status
        } catch (_: Exception) {
            throw HttpServiceException(null, "Unhandled exception")
        }
    }
}

