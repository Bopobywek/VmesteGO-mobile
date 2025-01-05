package ru.vmestego

import java.time.LocalDate

data class Ticket(val name: String, val date: LocalDate = LocalDate.now())

