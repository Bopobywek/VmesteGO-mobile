package ru.vmestego.bll.services.comments.models

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentRequest(
    val eventId: Long,
    val text: String
)