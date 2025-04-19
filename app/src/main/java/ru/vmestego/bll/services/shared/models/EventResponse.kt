package ru.vmestego.bll.services.shared.models

import kotlinx.serialization.Serializable
import ru.vmestego.core.EventStatus
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class EventResponse(
    val id: Long,
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dates: LocalDateTime,
    val location: String,
    val description: String,
    val ageRestriction: Int,
    val price: Double,
    val isPrivate: Boolean,
    val externalId: Long?,
    val creatorId: Long?,
    val creatorUsername: String,
    val categories: List<String>,
    val images: List<String>,
    val eventStatus: EventStatus?,
)