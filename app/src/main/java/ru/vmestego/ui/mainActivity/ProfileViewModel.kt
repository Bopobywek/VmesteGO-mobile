package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.bll.services.notifications.NotificationService
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.core.EventStatus
import ru.vmestego.data.SecureStorage
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

    private val _wantToGoEvents = MutableStateFlow<List<EventUi>>(listOf())
    val wantToGoEvents = _wantToGoEvents.asStateFlow()

    private val _goingToEvents = MutableStateFlow<List<EventUi>>(listOf())
    val goingToEvents = _goingToEvents.asStateFlow()

    private val _notGoingToEvents = MutableStateFlow<List<EventUi>>(listOf())
    val notGoingToEvents = _notGoingToEvents.asStateFlow()

    fun logout() {
        tokenDataProvider.removeToken()
    }

    private val _usersService = UsersService()
    private val _notificationsService = NotificationService()
    private val _eventsService = EventsService()

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

            _hasUnreadNotifications.update { notificationsUi.any { !it.isRead } }
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
                response.toUserUi()
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
                userInfoResponse.toUserUi()
            }
            _imageState.update { UUID.randomUUID() }
        }
    }

    fun markNotificationsAsRead(ids: List<Int>) {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            // TODO
        }
    }

    private fun getAllEvents() {
        val userId = tokenDataProvider.getUserIdFromToken()
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            var events = _eventsService.getEventsByStatus(token, userId, EventStatus.WantToGo)
            _wantToGoEvents.update {
                events.map {
                    it.toEventUi()
                }
            }

            events = _eventsService.getEventsByStatus(token, userId, EventStatus.Going)
            _goingToEvents.update {
                events.map {
                    it.toEventUi()
                }
            }

            events = _eventsService.getEventsByStatus(token, userId, EventStatus.NotGoing)
            _notGoingToEvents.update {
                events.map {
                    it.toEventUi()
                }
            }
        }
    }
}

