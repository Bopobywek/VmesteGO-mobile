package ru.vmestego.ui.mainActivity.friends.friendsTab

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.datasources.UsersPagingDataSource
import ru.vmestego.bll.services.friends.FriendsService
import ru.vmestego.ui.mainActivity.FriendRequestUi
import ru.vmestego.ui.mainActivity.toFriendRequestUi
import ru.vmestego.ui.models.UserUi
import ru.vmestego.utils.TokenDataProvider

class FriendsTabViewModel(application: Application) : AndroidViewModel(application) {
    var searchText by mutableStateOf("")
        private set

    private val _users = MutableStateFlow<List<UserUi>>(listOf())
    val users = _users.asStateFlow()

    private val _incomingFriendsRequests = MutableStateFlow<List<FriendRequestUi>>(listOf())
    val incomingFriendsRequests = _incomingFriendsRequests.asStateFlow()

    private val _outgoingFriendsRequests = MutableStateFlow<List<FriendRequestUi>>(listOf())
    val outgoingFriendsRequests = _outgoingFriendsRequests.asStateFlow()

    var usersPager = mutableStateOf<Pager<Int, UserUi>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val tokenDataProvider = TokenDataProvider(application)
    private val friendsService = FriendsService()

    init {
        setAllFriends()
        updateRequests()
    }

    private fun updateRequests() {
        val token = tokenDataProvider.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            val sentRequests = friendsService.getSentFriendRequests(token)
            _outgoingFriendsRequests.update {
                sentRequests.map {
                    it.toFriendRequestUi()
                }
            }

            val pendingRequests = friendsService.getIncomingFriendRequests(token)
            _incomingFriendsRequests.update {
                pendingRequests.map {
                    it.toFriendRequestUi()
                }
            }
        }
    }

    fun cancelFriendRequest(request: FriendRequestUi) {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            friendsService.cancelFriendRequest(token, request.id)

            _outgoingFriendsRequests.update {
                _outgoingFriendsRequests.value.toMutableList().apply {
                    remove(request)
                }
            }
        }
    }

    fun declineFriendRequest(request: FriendRequestUi) {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            friendsService.rejectFriendRequest(token, request.id)

            _incomingFriendsRequests.update {
                _incomingFriendsRequests.value.toMutableList().apply {
                    remove(request)
                }
            }
        }
    }

    fun acceptFriendRequest(request: FriendRequestUi) {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            friendsService.acceptFriendRequest(token, request.id)

            _incomingFriendsRequests.update {
                _incomingFriendsRequests.value.toMutableList().apply {
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
        val token = tokenDataProvider.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            val response = friendsService.getAllFriends(token)

            _users.update {
                response.map {
                    UserUi(
                        id = it.friendUserId,
                        name = it.friendUsername,
                        imageUrl = it.friendImageUrl
                    )
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

        val token = tokenDataProvider.getToken()!!
        usersPager.value = Pager(
            PagingConfig(pageSize = 50)
        ) {
            UsersPagingDataSource(
                token,
                query
            )
        }
    }
}


