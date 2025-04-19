package ru.vmestego.bll.services.notifications.models

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class NotificationResponse(
    val id: Long,
    val text: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val isRead: Boolean
)
