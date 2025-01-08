package ru.vmestego

import android.net.Uri
import java.time.LocalDate

data class TicketUi(
    val eventName: String,
    val locationName: String,
    val date: LocalDate = LocalDate.now(),
    val ticketUri : Uri)

