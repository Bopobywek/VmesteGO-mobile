package ru.vmestego.bll.services.events.models

import kotlinx.serialization.Serializable

@Serializable
data class GetEventUploadImageUrlResponse(
    val uploadUrl: String,
    val key: String,
    val orderIndex: Int)