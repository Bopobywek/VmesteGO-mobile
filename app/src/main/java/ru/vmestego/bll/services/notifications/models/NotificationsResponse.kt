package ru.vmestego.bll.services.notifications.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponse(
    val notifications: List<NotificationResponse>
)
