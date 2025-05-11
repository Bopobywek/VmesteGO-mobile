package ru.vmestego.bll.services.invitations.models

import kotlinx.serialization.Serializable
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.bll.services.users.models.UserResponse

@Serializable
data class InvitationResponse(
    val id: Long,
    val event: EventResponse,
    val sender: UserResponse,
    val receiver: UserResponse,
    val status: EventInvitationStatus
)