package ru.vmestego.bll.services.events.models

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmEventImageUploadRequest(
    val imageKey: String,
    val orderIndex: Int)