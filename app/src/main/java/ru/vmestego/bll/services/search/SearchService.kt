package ru.vmestego.bll.services.search

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.core.API_BASE_ADDRESS

class SearchService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3
    private val defaultPageSize = 10

    suspend fun getPublicEvents(
        token: String,
        query: String? = null,
        categoriesIds: List<String> = emptyList<String>(),
        page: Int = 1
    ): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/created-public") {
                contentType(ContentType.Application.Json)
                parameter("limit", defaultPageSize)
                parameter("offset", (page - 1) * defaultPageSize)
                parameter("q", query)
                for (categoryId in categoriesIds) {
                    parameter("categoryIds", categoryId)
                }
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<EventResponse>>()
        } catch (_: Exception) {
            return listOf()
        }
    }

    suspend fun getPrivateEvents(
        token: String,
        query: String? = null,
        categoriesIds: List<String> = emptyList<String>(),
        page: Int = 1
    ): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/created-private") {
                contentType(ContentType.Application.Json)
                parameter("limit", defaultPageSize)
                parameter("offset", (page - 1) * defaultPageSize)
                parameter("q", query)
                for (categoryId in categoriesIds) {
                    parameter("categoryIds", categoryId)
                }
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<EventResponse>>()
        } catch (_: Exception) {
            return listOf()
        }
    }

    suspend fun getJoinedPrivateEvents(
        token: String,
        query: String? = null,
        categoriesIds: List<String> = emptyList<String>(),
        page: Int = 1
    ): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/joined-private") {
                contentType(ContentType.Application.Json)
                parameter("limit", defaultPageSize)
                parameter("offset", (page - 1) * defaultPageSize)
                parameter("q", query)
                for (categoryId in categoriesIds) {
                    parameter("categoryIds", categoryId)
                }
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<EventResponse>>()
        } catch (_: Exception) {
            return listOf()
        }
    }

    suspend fun getOtherAdminsPublicEvents(
        token: String,
        query: String? = null,
        categoriesIds: List<String> = emptyList<String>(),
        page: Int = 1
    ): List<EventResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/events/other-admins-public") {
                contentType(ContentType.Application.Json)
                parameter("limit", defaultPageSize)
                parameter("offset", (page - 1) * defaultPageSize)
                parameter("q", query)
                for (categoryId in categoriesIds) {
                    parameter("categoryIds", categoryId)
                }
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
            return response.body<List<EventResponse>>()
        } catch (_: Exception) {
            return listOf()
        }
    }
}