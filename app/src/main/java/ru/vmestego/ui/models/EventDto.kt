package ru.vmestego.ui.models

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class EventDto (
    val name: String,
    val location: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startAt: LocalDateTime
)
