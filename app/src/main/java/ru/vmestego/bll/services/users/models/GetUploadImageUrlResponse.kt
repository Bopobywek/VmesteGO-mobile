package ru.vmestego.bll.services.users.models

import kotlinx.serialization.Serializable

@Serializable
data class GetUploadImageUrlResponse(
    val uploadUrl: String,
    val key: String)