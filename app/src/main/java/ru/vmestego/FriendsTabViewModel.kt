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
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
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