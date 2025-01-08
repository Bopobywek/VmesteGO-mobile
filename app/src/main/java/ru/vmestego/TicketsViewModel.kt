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

    init {
        Log.i("TicketsViewModelInit", "init")
    }

    suspend fun loadDataFromDb() {
        val ticketsDal = _ticketsRepository.getAllTicketsStream()
        ticketsDal.forEach {
                ticketDal ->
            _tickets.apply {
                add(
                    TicketUi(
                        ticketDal.eventName,
                        ticketDal.locationName,
                        ticketDal.eventDate,
                        Uri.parse(ticketDal.uri)))
            }
        }
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
                    uri = uri.toString()))
        }
        _tickets.apply {
            add(TicketUi("Последнее испытание", "КЗ Измайлово", randomDate, uri))
        }
    }

}