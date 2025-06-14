package ru.vmestego.ui.mainActivity.event

import ru.vmestego.core.EventStatus
import ru.vmestego.ui.mainActivity.search.CategoryUi
import java.time.LocalDateTime

data class EventUi(
    val id: Long,
    val eventName: String,
    val locationName: String,
    val dateTime: LocalDateTime,
    val description: String,
    val eventStatus: EventStatus?,
    val categories: List<CategoryUi>,
    val imageUrl: String? = null
)