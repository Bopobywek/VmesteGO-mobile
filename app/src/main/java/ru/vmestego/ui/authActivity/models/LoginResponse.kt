package ru.vmestego.ui.authActivity.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)
