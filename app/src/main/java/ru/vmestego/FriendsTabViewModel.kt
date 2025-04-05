package ru.vmestego

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.services.friends.FriendsService
import ru.vmestego.bll.services.users.UsersService
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
    private val usersService = UsersService()
    private val friendsService = FriendsService()

    init {
        setAllFriends()
        updateRequests()
    }

    private fun updateRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            val responseData = friendsService.getSentFriendRequests()

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
            friendsService.cancelFriendRequest(request.to.id.toLong())

            withContext(Dispatchers.Main) {
                _outcomingFriendsRequests.apply {
                    remove(request)
                }
            }
        }
    }

    fun declineFriendRequest(request: FriendRequestUi) {
        viewModelScope.launch(Dispatchers.IO) {
            friendsService.rejectFriendRequest(request.id)

            withContext(Dispatchers.Main) {
                _incomingFriendsRequests.apply {
                    remove(request)
                }
            }
        }
    }

    fun acceptFriendRequest(request: FriendRequestUi) {
        viewModelScope.launch(Dispatchers.IO) {
            friendsService.acceptFriendRequest(request.id)


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
            val responseData = usersService.getAllFriends()

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
            val responseData = usersService.findUsers(query)

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


