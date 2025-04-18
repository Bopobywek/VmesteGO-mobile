package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.data.SecureStorage
import ru.vmestego.event.EventUi
import ru.vmestego.utils.JwtUtil
import java.util.UUID

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val secureStorage = SecureStorage.getStorageInstance(application)
    private val applicationContext = application

    private val _userInfo = MutableStateFlow<UserUi?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _events = mutableStateListOf<EventUi>()
    val events: List<EventUi> = _events

    fun logout() {
        secureStorage.removeToken()
    }

    private val _usersService = UsersService()

    init {
        getUserInfo()
        getAllEvents()
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
        }
    }

    private fun getUserInfo() {
        val token = secureStorage.getToken()
        val userId = JwtUtil.getUserIdFromToken(token!!)

        if (userId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val response = _usersService.getUserInfoById(userId, token)

            _userInfo.update {
                UserUi(
                    name = response.username,
                    id = response.id,
                    imageUrl = response.imageUrl
                )
            }
        }
    }

    fun updateImage(imageBytes: ByteArray) {
        val token = secureStorage.getToken()
        val userId = JwtUtil.getUserIdFromToken(token!!)

        if (userId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val url = _usersService.getUploadImageUrl(userId, token)
            _usersService.putImage(url.uploadUrl, imageBytes)
            val userInfoResponse = _usersService.confirmImageUpload(userId, token, url.key)
            _userInfo.update {
                UserUi(
                    name = userInfoResponse.username,
                    id = userInfoResponse.id,
                    imageUrl = userInfoResponse.imageUrl + "?${UUID.randomUUID()}" // for force recomposition
                )
            }
        }
    }

    private fun getAllEvents() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response: HttpResponse = client.get("http://10.0.2.2:8080/events/want") {
//                contentType(ContentType.Application.Json)
//            }
//
//            val responseData = response.body<SearchEventsResponse>()
//
//            _events.clear()
//            responseData.events.forEach {
//                val date: LocalDate =
//                    Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
//                _events.apply {
//                    add(EventUi(it.id, it.title, it.location, date, it.description))
//                }
//            }
//        }
    }

}