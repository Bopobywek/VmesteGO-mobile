package ru.vmestego.data

import androidx.room.Embedded
import androidx.room.Relation

// https://stackoverflow.com/a/64108463
data class TicketWithEvent(
    @Embedded
    val ticket: Ticket,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "uid"
    )
    val event: Event
)