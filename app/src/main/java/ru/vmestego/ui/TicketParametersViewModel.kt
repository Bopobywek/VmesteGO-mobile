package ru.vmestego.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.TicketUi
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.Ticket
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

class TicketParametersViewModel(application: Application) : AndroidViewModel(application) {
    private val _tickets = mutableStateListOf<TicketUi>()
    val tickets: List<TicketUi> = _tickets

    private val _ticketsRepository : TicketsRepository = TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())

    init {
        Log.i("TicketParametersViewModel", "init")
    }

    fun addTicket(uri: Uri) {
        val minDay = LocalDate.of(1970, 1, 1).toEpochDay()
        val maxDay = LocalDate.of(2015, 12, 31).toEpochDay()
        val randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay)
        val randomDate = LocalDate.ofEpochDay(randomDay).withYear(2025)
        viewModelScope.launch(Dispatchers.IO) {
            _ticketsRepository.insert(
                Ticket(
                    eventName = "Последнее испытание",
                    locationName = "КЗ Измайлово",
                    eventDate = randomDate,
                    uri = uri.toString())
            )
        }
    }


}