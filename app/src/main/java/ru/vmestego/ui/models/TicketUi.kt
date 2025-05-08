package ru.vmestego.ui.models

import android.net.Uri
import java.time.LocalDateTime

data class TicketUi(
    val id: Long,
    val eventName: String,
    val locationName: String,
    val date: LocalDateTime,
    val ticketUri : Uri,
    var isOptionsRevealed: Boolean = false)

