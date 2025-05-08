package ru.vmestego.ui.models

import java.time.LocalDateTime

data class NotificationUi(
    val id: Long,
    val text: String,
    val createdAt: LocalDateTime,
    val isRead: Boolean
)
