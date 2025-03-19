package ru.vmestego.event

import java.time.LocalDate

data class EventUi(
    val eventName: String,
    val locationName: String,
    val date: LocalDate = LocalDate.now(),
    val description: String
)