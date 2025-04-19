package ru.vmestego.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.comments.CommentsService
import ru.vmestego.bll.services.comments.models.CommentResponse
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.core.EventStatus
import ru.vmestego.ui.mainActivity.toEventUi
import ru.vmestego.utils.TokenDataProvider
import java.time.LocalDateTime

class EventViewModel(application: Application, eventId: Long) : AndroidViewModel(application) {
    private val _eventId: Long = eventId

    private val _event = MutableStateFlow<EventUi?>(null)
    val event = _event.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentUi>>(listOf())
    val comments = _comments.asStateFlow()

    private val tokenDataProvider = TokenDataProvider(application)
    private val _eventsService = EventsService()
    private val _commentsService = CommentsService()


    init {
        getEvent()
        getAllComments()
    }

    private fun getEvent() {
        val token = tokenDataProvider.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            val event = _eventsService.getEventById(token, _eventId)
            _event.update {
                event?.toEventUi()
            }
        }
    }

    fun changeEventStatus(status: EventStatus) {
        val token = tokenDataProvider.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            _eventsService.changeEventStatus(token, _eventId, status)
            _event.update {
                _event.value?.copy(eventStatus = status)
            }
        }
    }

    private fun getAllComments() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val comments = _commentsService.getAllComments(token, _eventId)
            _comments.update {
                comments.map {
                    it.toCommentUi()
                }
            }
        }
    }

    fun addComment(text: String) {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            _commentsService.postComment(token, _eventId, text)

            val comments = _commentsService.getAllComments(token, _eventId)
            _comments.update {
                comments.map {
                    it.toCommentUi()
                }
            }
        }
    }
}

fun CommentResponse.toCommentUi(): CommentUi {
    return CommentUi(
        this.id,
        this.authorUsername,
        this.text,
        this.createdAt
    )
}

data class CommentUi(
    val id: Long,
    val username: String,
    val text: String,
    val createdAt: LocalDateTime
)

class EventViewModelFactory(val application: Application, val eventId: Long): ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(
            application, eventId
        ) as T
    }
}
