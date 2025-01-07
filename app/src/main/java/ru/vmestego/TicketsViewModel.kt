package ru.vmestego

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

// https://stackoverflow.com/a/71147730
// https://stackoverflow.com/a/67252955
class TicketsViewModel : ViewModel() {
    private val _tickets = mutableStateListOf<Ticket>()
    val tickets: List<Ticket> = _tickets

    fun addTicket() {
        val minDay = LocalDate.of(1970, 1, 1).toEpochDay()
        val maxDay = LocalDate.of(2015, 12, 31).toEpochDay()
        val randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay)
        val randomDate = LocalDate.ofEpochDay(randomDay).withYear(2025)
        _tickets.apply {
            add(Ticket("1", randomDate))
        }
    }

}