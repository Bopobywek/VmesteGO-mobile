package ru.vmestego.bll.services.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val imageUrl: String,
    val username: String,
    val role: String,
    val id: Int
)