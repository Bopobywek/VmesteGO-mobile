package ru.vmestego.data

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

data class EventDataDto(
    val name: String,
    val location: String,
    val startAt: LocalDateTime,
    val externalId: Long
)