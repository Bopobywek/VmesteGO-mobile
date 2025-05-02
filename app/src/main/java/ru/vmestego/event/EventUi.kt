package ru.vmestego.event

import ru.vmestego.core.EventStatus
import ru.vmestego.ui.mainActivity.CategoryUi
import java.time.LocalDateTime

data class EventUi(
    val id: Long,
    val eventName: String,
    val locationName: String,
    val dateTime: LocalDateTime,
    val description: String,
    val eventStatus: EventStatus?,
    val categories: List<CategoryUi>
)