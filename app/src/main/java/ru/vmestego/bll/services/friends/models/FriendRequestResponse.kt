package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable
import ru.vmestego.bll.services.users.models.UserResponse

@Serializable
data class FriendRequestResponse(
    val from: UserResponse,
    val to: UserResponse,
    val status: String,
    val id: Long
)