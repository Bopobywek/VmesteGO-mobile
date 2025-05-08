package ru.vmestego.ui.mainActivity.friends.feedTab

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.services.friends.FriendsService
import ru.vmestego.bll.services.friends.models.FriendsEventResponse
import ru.vmestego.ui.models.UserUi
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.mainActivity.toEventUi
import ru.vmestego.ui.mainActivity.toUserUi
import ru.vmestego.utils.TokenDataProvider

class FeedTabViewModel(application: Application) : AndroidViewModel(application) {
    private val _feedEvents = MutableStateFlow<List<FeedEventUi>>(listOf())
    val feedEvents = _feedEvents.asStateFlow()

    var isLoading = mutableStateOf(false)
        private set

    private val tokenDataProvider = TokenDataProvider(application)
    private val _friendsService = FriendsService()

    init {
        getFeedEvents()
    }

    private fun getFeedEvents() {
        val token = tokenDataProvider.getToken()!!

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val feedEvents = _friendsService.getFriendsEvents(token)
            _feedEvents.update {
                feedEvents.map {
                    it.toFeedEventUi()
                }
            }

            withContext(Dispatchers.Main) {
                isLoading.value = false
            }
        }
    }
}

fun FriendsEventResponse.toFeedEventUi(): FeedEventUi {
    return FeedEventUi(
        event = this.eventResponse.toEventUi(),
        users = this.friends.map { it.toUserUi() }
    )
}

data class FeedEventUi(
    val event: EventUi,
    val users: List<UserUi>
)