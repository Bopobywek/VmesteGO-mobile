package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.bll.services.friends.FriendsService
import ru.vmestego.bll.services.friends.models.FriendRequestResponse
import ru.vmestego.bll.services.friends.models.FriendRequestStatus
import ru.vmestego.bll.services.users.UsersService
import ru.vmestego.core.EventStatus
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.models.UserUi
import ru.vmestego.utils.TokenDataProvider
import ru.vmestego.utils.showShortToast

class OtherUserProfileViewModel(application: Application, userId: Long) :
    AndroidViewModel(application) {
    val currentUserId = userId

    private val _application = application

    private val _outgoingRequestStatus = MutableStateFlow(FriendRequestStatusUi.None)
    val outgoingRequestStatus = _outgoingRequestStatus.asStateFlow()

    private val _incomingRequestStatus = MutableStateFlow(FriendRequestStatusUi.None)
    val incomingRequestStatus = _incomingRequestStatus.asStateFlow()

    private val _friendRequest = MutableStateFlow<FriendRequestUi?>(null)
    private val _incomingFriendRequest = MutableStateFlow<FriendRequestUi?>(null)


    private val _userInfo = MutableStateFlow<UserUi?>(null)
    val userInfo = _userInfo.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    private val _wantToGoEvents = MutableStateFlow<List<EventUi>>(listOf())
    val wantToGoEvents = _wantToGoEvents.asStateFlow()

    private val _goingToEvents = MutableStateFlow<List<EventUi>>(listOf())
    val goingToEvents = _goingToEvents.asStateFlow()

    private val _notGoingToEvents = MutableStateFlow<List<EventUi>>(listOf())
    val notGoingToEvents = _notGoingToEvents.asStateFlow()

    private val tokenDataProvider = TokenDataProvider(application)
    private val _usersService = UsersService()
    private val _friendsService = FriendsService()
    private val _eventsService = EventsService()

    init {
        getUserInfo()
        updateStatuses()
        getAllEvents()
    }

    private fun getUserInfo() {
        val userId = tokenDataProvider.getUserIdFromToken()
        val token = tokenDataProvider.getToken()!!

        if (userId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = _usersService.getUserInfoById(currentUserId.toString(), token)

                _userInfo.update {
                    response.toUserUi()
                }
            } catch (_: Exception) {
                _application.showShortToast("Не удалось получить информацию об аккаунте, попробуйте позже")
            }
        }
    }

    fun changeRequestStatus() {
        val token = tokenDataProvider.getToken()!!
        val userId = tokenDataProvider.getUserIdFromToken()!!

        if (_outgoingRequestStatus.value == FriendRequestStatusUi.None) {
            viewModelScope.launch(Dispatchers.IO) {
                _friendsService.createFriendRequest(token, currentUserId.toString())
                val friendRequest =
                    _friendsService.getFriendRequest(token, userId, currentUserId.toString())
                _friendRequest.update {
                    friendRequest!!.toFriendRequestUi()
                }
                _outgoingRequestStatus.update {
                    FriendRequestStatusUi.Pending
                }
            }
        }

        if (_outgoingRequestStatus.value == FriendRequestStatusUi.Pending
            || _outgoingRequestStatus.value == FriendRequestStatusUi.Rejected
            || _outgoingRequestStatus.value == FriendRequestStatusUi.Done
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                _friendsService.cancelFriendRequest(token, _friendRequest.value!!.id)
                _outgoingRequestStatus.update {
                    FriendRequestStatusUi.None
                }
            }
        }
    }

    fun acceptIncomingRequest() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            _friendsService.acceptFriendRequest(token, _incomingFriendRequest.value!!.id)
            _incomingRequestStatus.update {
                FriendRequestStatusUi.Done
            }
        }
    }

    fun rejectIncomingRequest() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            _friendsService.rejectFriendRequest(token, _incomingFriendRequest.value!!.id)
            _incomingRequestStatus.update {
                FriendRequestStatusUi.Rejected
            }
        }
    }

    fun updateStatuses() {
        getIncomingRequestStatus()
        getOutgoingRequestStatus()
    }

    fun removeFriend() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            _friendsService.removeFriend(token, currentUserId)
            _incomingRequestStatus.update {
                FriendRequestStatusUi.Rejected
            }
        }
    }

    fun getOutgoingRequestStatus() {
        isLoading = true
        val token = tokenDataProvider.getToken()!!
        val userId = tokenDataProvider.getUserIdFromToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val friendRequest =
                _friendsService.getFriendRequest(token, userId, currentUserId.toString())

            if (friendRequest == null) {
                _outgoingRequestStatus.update {
                    FriendRequestStatusUi.None
                }
            } else {
                _friendRequest.update {
                    friendRequest.toFriendRequestUi()
                }
                _outgoingRequestStatus.update {
                    friendRequest.toFriendRequestUi().status
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    fun getIncomingRequestStatus() {
        isLoading = true
        val token = tokenDataProvider.getToken()!!
        val userId = tokenDataProvider.getUserIdFromToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val friendRequest =
                _friendsService.getFriendRequest(token, currentUserId.toString(), userId)

            if (friendRequest == null) {
                _incomingRequestStatus.update {
                    FriendRequestStatusUi.None
                }
            } else {
                _incomingFriendRequest.update {
                    friendRequest.toFriendRequestUi()
                }
                _incomingRequestStatus.update {
                    friendRequest.toFriendRequestUi().status
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    private fun getAllEvents() {
        val currentUserIdString = currentUserId.toString()
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            var events =
                _eventsService.getEventsByStatus(token, currentUserIdString, EventStatus.WantToGo)
            _wantToGoEvents.update {
                events.map {
                    it.toEventUi()
                }
            }

            events = _eventsService.getEventsByStatus(token, currentUserIdString, EventStatus.Going)
            _goingToEvents.update {
                events.map {
                    it.toEventUi()
                }
            }

            events =
                _eventsService.getEventsByStatus(token, currentUserIdString, EventStatus.NotGoing)
            _notGoingToEvents.update {
                events.map {
                    it.toEventUi()
                }
            }
        }
    }
}

fun FriendRequestResponse.toFriendRequestUi(): FriendRequestUi {
    return FriendRequestUi(
        id = this.id,
        from = this.sender.toUserUi(),
        to = this.receiver.toUserUi(),
        status = this.status.toFriendRequestStatusUi()
    )
}

fun FriendRequestStatus.toFriendRequestStatusUi(): FriendRequestStatusUi {
    when {
        this == FriendRequestStatus.Accepted -> {
            return FriendRequestStatusUi.Done
        }

        this == FriendRequestStatus.Pending -> {
            return FriendRequestStatusUi.Pending
        }

        this == FriendRequestStatus.Rejected -> {
            return FriendRequestStatusUi.Rejected
        }
    }

    return FriendRequestStatusUi.None
}

class OtherUserProfileViewModelFactory(val application: Application, val userId: Long) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OtherUserProfileViewModel(
            application, userId
        ) as T
    }
}