package ru.vmestego

import android.net.Uri
import java.time.LocalDate

data class TicketUi(
    val id: Long,
    val eventName: String,
    val locationName: String,
    val date: LocalDate = LocalDate.now(),
    val ticketUri : Uri,
    var isOptionsRevealed: Boolean = false)

