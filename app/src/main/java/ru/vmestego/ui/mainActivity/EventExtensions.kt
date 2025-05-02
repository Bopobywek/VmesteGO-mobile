package ru.vmestego.ui.mainActivity

import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.event.EventUi

fun EventResponse.toEventUi(): EventUi {
    return EventUi(
        id = this.id,
        eventName = this.title,
        locationName = this.location,
        dateTime = this.dates,
        description = this.description,
        eventStatus = this.eventStatus,
        categories = categories.map { it.toCategoryUi() }
    )
}