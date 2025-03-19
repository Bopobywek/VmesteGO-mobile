package ru.vmestego

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.vmestego.data.SecureStorage

class FriendsTabViewModel(application: Application) : AndroidViewModel(application) {
    var searchText by mutableStateOf("")
        private set

    private val _users = mutableStateListOf<UserUi>()
    val users: List<UserUi> = _users

    private val _incomingFriendsRequests = mutableStateListOf<FriendRequestUi>()
    val incomingFriendsRequests: List<FriendRequestUi> = _incomingFriendsRequests

    private val _outcomingFriendsRequests = mutableStateListOf<FriendRequestUi>()
    val outcomingFriendsRequests: List<FriendRequestUi> = _outcomingFriendsRequests

    var isLoading by mutableStateOf(false)
        private set

    private val secureStorage = SecureStorage.getStorageInstance(application)

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    init {
        setAllFriends()
        updateRequests()
    }

    private fun updateRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/requests/sent") {
                contentType(ContentType.Application.Json)
            }

            if (response.status != HttpStatusCode.OK)
                return@launch

            val responseData = response.body<FriendRequestsResponse>()
            withContext(Dispatchers.Main) {
                _outcomingFriendsRequests.clear()
                responseData.requests.forEach { fr ->
                    val uiModel = FriendRequestUi(
                        from = UserUi(imageUrl = fr.from.imageUrl, name = fr.from.name, fr.from.id),
                        to = UserUi(imageUrl = fr.to.imageUrl, name = fr.to.name, fr.to.id),
                        id = fr.id,
                        status = fr.status
                    )
                    _outcomingFriendsRequests.apply {
                        add(uiModel)
                    }
                }
            }
        }
    }

    fun cancelFriendRequest(request: FriendRequestUi) {
        viewModelScope.launch(Dispatchers.IO) {
            // requests/${request.to.id} -- удалить запрос на дружбу к пользователю
            val response: HttpResponse =
                client.delete("http://10.0.2.2:8080/requests/${request.to.id}") {
                    contentType(ContentType.Application.Json)
                }

            if (response.status != HttpStatusCode.OK) {
                return@launch
            }

            withContext(Dispatchers.Main) {
                _outcomingFriendsRequests.apply {
                    remove(request)
                }
            }
        }
    }

    fun declineFriendRequest(request: FriendRequestUi) {
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/requests/{requestId:int}/reject") {
                contentType(ContentType.Application.Json)
            }

            if (response.status != HttpStatusCode.OK)
                return@launch

            withContext(Dispatchers.Main) {
                _incomingFriendsRequests.apply {
                    remove(request)
                }
            }
        }
    }

    fun acceptFriendRequest(request: FriendRequestUi) {
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/requests/{requestId:int}/accept") {
                contentType(ContentType.Application.Json)
            }

            if (response.status != HttpStatusCode.OK)
                return@launch

            withContext(Dispatchers.Main) {
                _incomingFriendsRequests.apply {
                    remove(request)
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        searchText = newQuery
        onSearch(newQuery)
    }

    private fun setAllFriends() {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/friends") {
                contentType(ContentType.Application.Json)
            }

            val responseData = response.body<UsersSearchResponse>()
            _users.clear()
            responseData.users.forEach {
                _users.apply {
                    add(UserUi(it.imageUrl, it.name, it.id))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    fun onSearch(query: String) {
        if (query.isEmpty()) {
            setAllFriends()
            return
        }

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/users/search") {
                url {
                    parameters["q"] = query
                }

                contentType(ContentType.Application.Json)
            }

            val responseData = response.body<UsersSearchResponse>()
            Log.i("Users", responseData.users.size.toString())
            _users.clear()
            responseData.users.forEach {
                _users.apply {
                    add(UserUi(it.imageUrl, it.name, it.id))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }
}

@Serializable
data class UsersSearchResponse(
    val users: List<UserResponse>
)

@Serializable
data class UserResponse(
    val imageUrl: String,
    val name: String,
    val id: Int
)

data class UserUi(
    val imageUrl: String,
    val name: String,
    val id: Int
)


@Serializable
data class FriendRequestsResponse(
    val requests: List<FriendRequestResponse>
)

@Serializable
data class FriendRequestResponse(
    val from: UserResponse,
    val to: UserResponse,
    val status: String,
    val id: Long
)

data class FriendRequestUi(
    val from: UserUi,
    val to: UserUi,
    val status: String,
    val id: Long
)