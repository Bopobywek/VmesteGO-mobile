package ru.vmestego.ui.mainActivity

import java.time.LocalDateTime

data class NotificationUi(
    val id: Long,
    val text: String,
    val createdAt: LocalDateTime,
    val isRead: Boolean
)
