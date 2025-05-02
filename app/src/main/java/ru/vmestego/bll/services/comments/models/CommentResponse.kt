package ru.vmestego.bll.services.comments.models

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class CommentResponse(
    val id: Long,
    val authorId: Long,
    val authorUsername: String,
    val text: String,
    val rating: Int,
    val userRating: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)