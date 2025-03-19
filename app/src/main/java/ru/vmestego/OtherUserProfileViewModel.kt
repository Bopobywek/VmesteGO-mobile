package ru.vmestego

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OtherUserProfileViewModel(application: Application, userId: Int) : AndroidViewModel(application) {
    val currentUserId = userId

    var requestStatus by mutableStateOf(RequestStatus.NONE)
        private set

    var username by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    init {
        getRequestStatus()
    }

    fun changeRequestStatus() {
        if (requestStatus == RequestStatus.NONE) {
            viewModelScope.launch(Dispatchers.IO) {
                val response: HttpResponse =
                    client.post("http://10.0.2.2:8080/requests/${currentUserId}") {
                        contentType(ContentType.Application.Json)
                    }

                if (response.status != HttpStatusCode.OK) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    requestStatus = RequestStatus.PENDING
                }
            }
        } else if (requestStatus == RequestStatus.PENDING) {
            // TODO: может быть кейс, когда не обновилось, но данные на сервере поменялись
            viewModelScope.launch(Dispatchers.IO) {
                // requests/${currentUserId} -- удалить запрос на дружбу к пользователю, чья страничка
                val response: HttpResponse =
                    client.delete("http://10.0.2.2:8080/requests/${currentUserId}") {
                        contentType(ContentType.Application.Json)
                    }

                if (response.status != HttpStatusCode.OK) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    requestStatus = RequestStatus.NONE
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val response: HttpResponse =
                    client.delete("http://10.0.2.2:8080/friends/${currentUserId}") {
                        contentType(ContentType.Application.Json)
                    }

                if (response.status != HttpStatusCode.OK) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    requestStatus = RequestStatus.NONE
                }
            }
        }
    }

    fun getRequestStatus() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val userResponse: HttpResponse = client.get("http://10.0.2.2:8080/users/${currentUserId}") {
                contentType(ContentType.Application.Json)
            }
            val userData = userResponse.body<UserResponse>()

            val response: HttpResponse = client.get("http://10.0.2.2:8080/requests/${currentUserId}") {
                contentType(ContentType.Application.Json)
            }

            val responseData = response.body<RequestStatusResponse>()
            withContext(Dispatchers.Main) {
                username = userData.name
                when {
                    responseData.status.lowercase() == "done" -> {
                        requestStatus = RequestStatus.DONE
                    }
                    responseData.status.lowercase() == "pending" -> {
                        requestStatus = RequestStatus.PENDING
                    }
                    responseData.status.lowercase() == "none" -> {
                        requestStatus = RequestStatus.NONE
                    }
                }
                isLoading = false
            }
        }
    }
}

@Serializable
data class RequestStatusResponse(val status: String)

enum class RequestStatus {
    DONE, PENDING, NONE
}

class OtherUserProfileViewModelFactory(val application: Application, val userId: Int): ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OtherUserProfileViewModel(
            application, userId
        ) as T
    }
}