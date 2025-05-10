package ru.vmestego.ui.mainActivity.friends.invitationsTab

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.invitations.models.EventInvitationStatus
import ru.vmestego.bll.services.invitations.models.InvitationResponse
import ru.vmestego.bll.services.invitations.InvitationsService
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.Event
import ru.vmestego.data.EventDataDto
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.ui.models.UserUi
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.mainActivity.toEventUi
import ru.vmestego.ui.mainActivity.toUserUi
import ru.vmestego.utils.TokenDataProvider

class InvitationsTabViewModel(application: Application) : AndroidViewModel(application) {
    private val _pendingInvitations = MutableStateFlow<List<InvitationUi>>(listOf())
    val pendingInvitations = _pendingInvitations.asStateFlow()

    private val _sentInvitations = MutableStateFlow<List<InvitationUi>>(listOf())
    val sentInvitations = _sentInvitations.asStateFlow()

    private val _tokenDataHandler = TokenDataProvider(application)
    private val _invitationsService = InvitationsService()
    private val _eventsRepository: EventsRepositoryImpl =
        EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    init {
        getPendingInvitations()
        getSentInvitations()
    }

    private fun getPendingInvitations() {
        val token = _tokenDataHandler.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            val invitations = _invitationsService.getPendingInvitations(token)
            _pendingInvitations.update {
                invitations.map {
                    it.toInvitationUi()
                }
            }
        }
    }

    private fun getSentInvitations() {
        val token = _tokenDataHandler.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            val invitations = _invitationsService.getSentInvitations(token)
            _sentInvitations.update {
                invitations.map {
                    it.toInvitationUi()
                }
            }
        }
    }

    fun acceptInvite(invitationsUi: List<InvitationUi>) {
        val token = _tokenDataHandler.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            for (i in invitationsUi) {
                _invitationsService.acceptInvitation(token, i.id)
            }
            _pendingInvitations.update {
                val updated = _pendingInvitations.value.toMutableList()
                updated -= invitationsUi
                updated
            }
        }
    }

    fun rejectInvite(invitationsUi: List<InvitationUi>) {
        val token = _tokenDataHandler.getToken()!!
        viewModelScope.launch(Dispatchers.IO) {
            for (i in invitationsUi) {
                _invitationsService.rejectInvitation(token, i.id)
            }
            _pendingInvitations.update {
                val updated = _pendingInvitations.value.toMutableList()
                updated -= invitationsUi
                updated
            }
        }
    }

    fun cancelInvitation(invitationUi: InvitationUi) {
        val token = _tokenDataHandler.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            _invitationsService.revokeInvitation(token, invitationUi.id)

            _sentInvitations.update {
                val updated = _sentInvitations.value.toMutableList()
                updated -= invitationUi
                updated
            }
        }
    }

    suspend fun addEvent(eventDto: EventDataDto): Long {
        var existingEvent = getEventByExternalId(eventDto.externalId.toInt())
        if (existingEvent != null) {
            return existingEvent.uid.toLong()
        }
        return _eventsRepository.insert(
            Event(
                externalId = eventDto.externalId.toInt(),
                title = eventDto.name,
                location = eventDto.location,
                startAt = eventDto.startAt.plusHours(3),
                isSynchronized = false
            )
        )
    }

    suspend fun getEventByExternalId(externalId: Int): Event? {
        val result = _eventsRepository.getByExternalId(externalId)
        if (result.isEmpty()) {
            return null
        }
        return result[0]
    }
}

fun InvitationResponse.toInvitationUi(): InvitationUi {
    return InvitationUi(
        id = id,
        event = event.toEventUi(),
        sender = sender.toUserUi(),
        receiver = receiver.toUserUi(),
        status = status
    )
}

data class InvitationUi(
    val id: Long,
    val event: EventUi,
    val sender: UserUi,
    val receiver: UserUi,
    val status: EventInvitationStatus
)