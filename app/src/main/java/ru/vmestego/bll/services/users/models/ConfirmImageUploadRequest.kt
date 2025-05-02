package ru.vmestego.bll.services.users.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmImageUploadRequest(
    val key: String)