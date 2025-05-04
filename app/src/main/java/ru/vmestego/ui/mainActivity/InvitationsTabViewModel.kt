package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.invitations.EventInvitationStatus
import ru.vmestego.bll.services.invitations.InvitationResponse
import ru.vmestego.bll.services.invitations.InvitationsService
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.utils.TokenDataProvider

class InvitationsTabViewModel(application: Application) : AndroidViewModel(application) {
    private val _pendingInvitations = MutableStateFlow<List<InvitationUi>>(listOf())
    val pendingInvitations = _pendingInvitations.asStateFlow()

    private val _sentInvitations = MutableStateFlow<List<InvitationUi>>(listOf())
    val sentInvitations = _sentInvitations.asStateFlow()

    private val _tokenDataHandler = TokenDataProvider(application)
    private val _invitationsService = InvitationsService()

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