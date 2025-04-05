package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestsResponse(
    val requests: List<FriendRequestResponse>
)