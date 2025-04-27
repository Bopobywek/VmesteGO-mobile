package ru.vmestego.ui.ticketActivity

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.data.Ticket
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import ru.vmestego.utils.TokenDataProvider

class TicketParametersViewModel(application: Application) : AndroidViewModel(application) {
    private val _ticketsRepository: TicketsRepository =
        TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())
    private val _eventsRepository: EventsRepositoryImpl =
        EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())
    private val _tokenDataProvider = TokenDataProvider(application)

    init {
        Log.i("TicketParametersViewModel", "init")
    }


    fun addTicket(uri: Uri, eventId: Long) {
        var userId = _tokenDataProvider.getUserIdFromToken()!!.toLong()

        viewModelScope.launch(Dispatchers.IO) {
            _ticketsRepository.insert(
                Ticket(
                    eventId = eventId,
                    uri = uri.toString(),
                    userId = userId
                )
            )
        }
    }
}