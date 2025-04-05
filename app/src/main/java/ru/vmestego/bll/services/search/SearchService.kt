package ru.vmestego.bll.services.search

import android.widget.Toast
import androidx.room.Query
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.search.models.SearchEventsResponse

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

    suspend fun searchEvents(query: String): SearchEventsResponse {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/search") {
                url {
                    parameters["q"] = query
                }
                contentType(ContentType.Application.Json)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return SearchEventsResponse(listOf())
        }

        return response.body<SearchEventsResponse>()
    }

    suspend fun getAllEvents(): SearchEventsResponse {
        val response: HttpResponse
        try {
            response = client.get("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return SearchEventsResponse(listOf())
        }

        return response.body<SearchEventsResponse>()
    }
}