package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateInvitationRequest(
    val eventId: Long,
    val receiverId: Long)