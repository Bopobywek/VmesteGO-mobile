package ru.vmestego

import android.net.Uri
import java.time.LocalDate

data class Ticket(
    val name: String,
    val date: LocalDate = LocalDate.now(),
    val ticketUri : Uri)

