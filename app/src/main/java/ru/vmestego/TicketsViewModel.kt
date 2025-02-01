package ru.vmestego

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.Ticket
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

// https://stackoverflow.com/a/71147730
// https://stackoverflow.com/a/67252955
class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val _tickets = mutableStateListOf<TicketUi>()
    val tickets: List<TicketUi> = _tickets

    private val _ticketsRepository : TicketsRepository = TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())

    // TODO: может ли быть такая ситуация, что мы в UI уже забрали список, который не успел прогрузиться в init
    init {
        Log.i("TicketsViewModelInit", "init")
        viewModelScope.launch(Dispatchers.IO)
        {
            loadDataFromDb()
        }
    }

    suspend fun loadDataFromDb() {
        val ticketsDal = _ticketsRepository.getAllTicketsWithEvents()
        ticketsDal.forEach {
                ticketDal ->
            _tickets.apply {
                add(
                    TicketUi(
                        ticketDal.event.title,
                        ticketDal.event.location,
                        ticketDal.event.startAt.toLocalDate(),
                        Uri.parse(ticketDal.ticket.uri)))
            }
        }
    }

    fun addTicket(uri: Uri, eventId: Long) {
//        val minDay = LocalDate.of(1970, 1, 1).toEpochDay()
//        val maxDay = LocalDate.of(2015, 12, 31).toEpochDay()
//        val randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay)
//        val randomDate = LocalDate.ofEpochDay(randomDay).withYear(2025)
        viewModelScope.launch(Dispatchers.IO) {
            val id = _ticketsRepository.insert(
                Ticket(
                    eventId = eventId,
                    uri = uri.toString()))

            val ticket = _ticketsRepository.getTicketWithEvent(id)

            _tickets.apply {
                add(TicketUi(ticket.event.title, ticket.event.location, ticket.event.startAt.toLocalDate(), uri))
            }
        }
    }

}