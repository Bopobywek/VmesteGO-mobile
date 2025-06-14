package ru.vmestego.ui.mainActivity.tickets

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.TicketWithEvent
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import ru.vmestego.ui.models.TicketUi
import ru.vmestego.utils.TokenDataProvider

// https://stackoverflow.com/a/71147730
// https://stackoverflow.com/a/67252955
class TicketsViewModel(application: Application) : AndroidViewModel(application) {
    private val _tickets = MutableStateFlow<List<TicketUi>?>(null)
    val tickets = _tickets.asStateFlow()

    private val _ticketsRepository: TicketsRepository =
        TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())

    private val _tokenDataProvider = TokenDataProvider(application)

    init {
        loadDataFromDb()
    }

    private fun loadDataFromDb() {
        var userId = _tokenDataProvider.getUserIdFromToken()
        if (userId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            _ticketsRepository.getAllTicketsWithEvents(userId.toLong()).flowOn(Dispatchers.IO)
                .collect { tickets: List<TicketWithEvent> ->
                    val ticketsUi = tickets.map {
                        TicketUi(
                            it.ticket.uid,
                            it.event.title,
                            it.event.location,
                            it.event.startAt,
                            Uri.parse(it.ticket.uri)
                        )
                    }
                    _tickets.update { ticketsUi }
                }
        }
    }

    fun removeTicket(ticket: TicketUi) {
        viewModelScope.launch(Dispatchers.IO) {
            _ticketsRepository.deleteTicketById(ticket.id)
        }
    }
}