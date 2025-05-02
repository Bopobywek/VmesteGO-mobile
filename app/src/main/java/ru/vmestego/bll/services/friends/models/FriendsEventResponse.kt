package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.bll.services.users.models.UserResponse

@Serializable
data class FriendsEventResponse(
    val eventResponse: EventResponse,
    val friends: List<UserResponse>
)