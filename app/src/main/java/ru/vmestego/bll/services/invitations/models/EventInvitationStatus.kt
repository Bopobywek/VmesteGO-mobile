package ru.vmestego.bll.services.invitations.models

import kotlinx.serialization.Serializable

@Serializable
enum class EventInvitationStatus {
    Pending,
    Accepted,
    Rejected
}