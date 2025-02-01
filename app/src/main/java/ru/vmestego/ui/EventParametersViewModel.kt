package ru.vmestego.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.TicketUi
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.Event
import ru.vmestego.data.EventDataDto
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.data.Ticket
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl
import ru.vmestego.ui.models.EventDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

class EventParametersViewModel(application: Application) : AndroidViewModel(application) {
    private val _tickets = mutableStateListOf<TicketUi>()
    val tickets: List<TicketUi> = _tickets

    private val _ticketsRepository: TicketsRepository =
        TicketsRepositoryImpl(AppDatabase.getDatabase(application).ticketDao())
    private val _eventsRepository: EventsRepositoryImpl =
        EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    init {
        Log.i("TicketParametersViewModel", "init")
    }

    suspend fun addEvent(eventDto: EventDataDto): Long {
        return _eventsRepository.insert(
            Event(
                externalId = 1,
                title = eventDto.name,
                location = eventDto.location,
                startAt = eventDto.startAt,
                isSynchronized = false
            )
        )
    }
}