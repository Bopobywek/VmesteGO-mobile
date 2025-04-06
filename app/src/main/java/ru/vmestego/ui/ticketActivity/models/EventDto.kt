package ru.vmestego.ui.ticketActivity.models

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class EventDto (
    val uid: Long,
    val name: String,
    val location: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startAt: LocalDateTime
)
