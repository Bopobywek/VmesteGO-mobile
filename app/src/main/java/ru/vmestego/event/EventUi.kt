package ru.vmestego.event

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class EventUi(
    val id: Long,
    val eventName: String,
    val locationName: String,
    @Contextual val date: LocalDate = LocalDate.now(),
    val description: String
)