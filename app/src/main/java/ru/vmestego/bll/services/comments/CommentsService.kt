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
            response = client.get("http://10.0.2.2:8080/comments/${eventId}") {
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
            response = client.post("http://10.0.2.2:8080/comments") {
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
            response = client.delete("http://10.0.2.2:8080/comments/${commentId}") {
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

