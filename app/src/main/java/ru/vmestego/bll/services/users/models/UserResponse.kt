package ru.vmestego.bll.services.users.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val imageUrl: String,
    val name: String,
    val id: Int
)