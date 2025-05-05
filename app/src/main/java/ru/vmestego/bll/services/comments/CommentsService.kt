package ru.vmestego.bll.services.comments

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.comments.models.CommentResponse
import ru.vmestego.bll.services.comments.models.PostCommentRequest
import ru.vmestego.core.API_BASE_ADDRESS

class CommentsService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val retryNumber = 3;

    suspend fun getAllComments(token: String, eventId: Long) : List<CommentResponse> {
        val response: HttpResponse
        try {
            response = client.get("${API_BASE_ADDRESS}/comments/${eventId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
            return listOf()
        }

        return response.body<List<CommentResponse>>()
    }

    suspend fun postComment(token: String, eventId: Long, text: String) {
        val request = PostCommentRequest(eventId, text)

        val response: HttpResponse
        try {
            response = client.post("${API_BASE_ADDRESS}/comments") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(request)
                retry {
                    retryOnExceptionOrServerErrors(retryNumber)
                }
            }
        } catch (_: Exception) {
        }
    }

    suspend fun removeComment(token: String, commentId: Long) {
        val response: HttpResponse
        try {
            response = client.delete("${API_BASE_ADDRESS}/comments/${commentId}") {
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

