package ru.vmestego.ui.authActivity.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String
)
