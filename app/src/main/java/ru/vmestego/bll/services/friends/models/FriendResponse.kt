package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    val id: Long,
    val friendUserId: Long,
    val friendUsername: String,
    val friendImageUrl: String
)
