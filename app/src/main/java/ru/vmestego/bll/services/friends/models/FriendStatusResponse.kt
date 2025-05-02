package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable
import ru.vmestego.bll.services.users.models.UserResponse
import ru.vmestego.core.EventStatus

@Serializable
data class FriendStatusResponse(
    val friend: UserResponse,
    val eventStatus: EventStatus?
)