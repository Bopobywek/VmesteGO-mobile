package ru.vmestego.bll.services.friends.models

import kotlinx.serialization.Serializable

@Serializable
data class SendRequestForUserRequest(
    val receiverId: Long
)