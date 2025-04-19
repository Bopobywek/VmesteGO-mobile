package ru.vmestego.ui.mainActivity

import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.core.EventStatus
import ru.vmestego.event.EventUi

fun EventResponse.toEventUi(): EventUi {
    return EventUi(
        id = this.id,
        eventName = this.title,
        locationName = this.location,
        date = this.dates.toLocalDate(),
        description = this.description,
        eventStatus = this.eventStatus ?: EventStatus.NotGoing
    )
}