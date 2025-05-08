package ru.vmestego.bll.services.events.models

import kotlinx.serialization.Serializable
import ru.vmestego.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class CreateEventRequest(
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class) val dates: LocalDateTime,
    val location: String,
    val description: String,
    val ageRestriction: Int,
    val price: Double,
    val isPrivate: Boolean,
    val eventCategoryNames: List<String> = emptyList(),
    val eventImages: List<String> = emptyList(),
    val externalId: Int? = null
)