package ru.vmestego.bll.services.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UsersSearchResponse(
    val users: List<UserResponse>
)