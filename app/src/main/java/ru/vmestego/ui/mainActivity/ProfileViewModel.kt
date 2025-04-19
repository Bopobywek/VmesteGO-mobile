package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.notifications.NotificationService
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.event.EventUi
import ru.vmestego.utils.TokenDataProvider
import java.util.UUID

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDataProvider = TokenDataProvider(application)

    private val _userInfo = MutableStateFlow<UserUi?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationUi>>(listOf())
    val notifications = _notifications.asStateFlow()

    private val _hasUnreadNotifications = MutableStateFlow<Boolean>(false)
    val hasUnreadNotifications = _hasUnreadNotifications.asStateFlow()

    private val _imageState = MutableStateFlow<UUID>(UUID.randomUUID())
    val imageState = _imageState.asStateFlow()

    private val _events = mutableStateListOf<EventUi>()
    val events: List<EventUi> = _events

    fun logout() {
        tokenDataProvider.removeToken()
    }

    private val _usersService = UsersService()
    private val _notificationsService = NotificationService()

    init {
        getUserInfo()
        getAllNotifications()
        getAllEvents()
    }


    private fun getAllNotifications() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val response = _notificationsService.getAllNotifications(token)

            val notificationsUi = response.notifications.map {
                NotificationUi(
                    it.id,
                    it.text,
                    it.createdAt,
                    it.isRead
                )
            }

            _notifications.update {
                notificationsUi
            }

            if (notificationsUi.any { !it.isRead }) {
                _hasUnreadNotifications.update { true }
            }
        }
    }

    private fun getUserInfo() {
        val userId = tokenDataProvider.getUserIdFromToken()
        val token = tokenDataProvider.getToken()!!

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
        val userId = tokenDataProvider.getUserIdFromToken()
        val token = tokenDataProvider.getToken()!!

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
                    imageUrl = userInfoResponse.imageUrl
                )
            }
            _imageState.update { UUID.randomUUID() }
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