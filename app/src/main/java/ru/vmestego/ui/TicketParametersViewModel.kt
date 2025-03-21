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
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.data.Ticket
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

class TicketParametersViewModel(application: Application) : AndroidViewModel(application) {
    private val _ticketsRepository: TicketsRepository =
        TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())
    private val _eventsRepository: EventsRepositoryImpl =
        EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    init {
        Log.i("TicketParametersViewModel", "init")
    }


    fun addTicket(uri: Uri, eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = _ticketsRepository.insert(
                Ticket(
                    eventId = eventId,
                    uri = uri.toString()
                )
            )
            // TODO: нужно обновлять коллекцию в другой модельке ещё
        }
    }


}