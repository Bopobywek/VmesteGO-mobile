package ru.vmestego.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)
