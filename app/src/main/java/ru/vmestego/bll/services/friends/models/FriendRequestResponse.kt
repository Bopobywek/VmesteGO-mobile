package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable
import ru.vmestego.bll.services.users.models.UserResponse
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class FriendRequestResponse(
    val id: Long,
    val sender: UserResponse,
    val receiver: UserResponse,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val status: FriendRequestStatus
)
