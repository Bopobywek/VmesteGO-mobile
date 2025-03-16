package ru.vmestego.auth.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String
)
