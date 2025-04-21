package ru.vmestego.bll.services.search

import android.widget.Toast
import androidx.room.Query
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.shared.models.EventResponse

class SearchService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3;

//    suspend fun searchEvents(query: String): SearchEventsResponse {
//        val response: HttpResponse
//        try {
//            response = client.get("http://10.0.2.2:8080/search") {
//                url {
//                    parameters["q"] = query
//                }
//                contentType(ContentType.Application.Json)
//                retry {
//                    retryOnExceptionOrServerErrors(retryNumber)
//                }
//            }
//        } catch (_: Exception) {
//            return SearchEventsResponse(listOf())
//        }
//
//        return response.body<SearchEventsResponse>()
//    }

    suspend fun getAllEvents(token: String): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (e: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }

    suspend fun getPublicEvents(token: String): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/created-public") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (e: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }

    suspend fun getPrivateEvents(token: String): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/created-private") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (e: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }

    suspend fun getJoinedPrivateEvents(token: String): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/joined-private") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (e: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }

    suspend fun getOtherAdminsPublicEvents(token: String): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events/other-admins-public") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (e: Exception) {
            return listOf()
        }

        return response.body<List<EventResponse>>()
    }
}