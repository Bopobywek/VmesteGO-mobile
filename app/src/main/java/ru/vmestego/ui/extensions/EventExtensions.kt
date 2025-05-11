package ru.vmestego.ui.extensions

import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.mainActivity.search.toCategoryUi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun EventResponse.toEventUi(): EventUi {
    return EventUi(
        id = this.id,
        eventName = this.title,
        locationName = this.location,
        dateTime = this.dates.toLdtAtCurrentTz(),
        description = this.description,
        eventStatus = this.eventStatus,
        categories = categories.map { it.toCategoryUi() },
        imageUrl = this.images.getOrNull(0)
    )
}

// https://stackoverflow.com/questions/49853999/convert-zoneddatetime-to-localdatetime-at-time-zone
fun LocalDateTime.toLdtAtCurrentTz(): LocalDateTime {
    val utcZoned = ZonedDateTime.of(this, ZoneOffset.UTC)
    val currentZone = ZoneId.systemDefault()
    val currentZoned = utcZoned.withZoneSameInstant(currentZone)
    return currentZoned.toLocalDateTime()
}