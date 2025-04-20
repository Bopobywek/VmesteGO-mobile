package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable

@Serializable
enum class FriendRequestStatus {
    Pending,
    Accepted,
    Rejected
}