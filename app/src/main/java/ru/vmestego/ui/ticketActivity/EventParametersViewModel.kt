package ru.vmestego.ui.ticketActivity

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.ui.mainActivity.TicketUi
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.Event
import ru.vmestego.data.EventDataDto
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.data.TicketsRepository
import ru.vmestego.data.TicketsRepositoryImpl

class EventParametersViewModel(application: Application) : AndroidViewModel(application) {
    private val _eventsRepository: EventsRepositoryImpl =
        EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    init {
        Log.i("TicketParametersViewModel", "init")
    }

    suspend fun addEvent(eventDto: EventDataDto): Long {
        var existingEvent = getEventByExternalId(eventDto.externalId.toInt())
        if (existingEvent != null) {
            return existingEvent.uid.toLong()
        }
        return _eventsRepository.insert(
            Event(
                externalId = eventDto.externalId.toInt(),
                title = eventDto.name,
                location = eventDto.location,
                startAt = eventDto.startAt,
                isSynchronized = false
            )
        )
    }

    suspend fun getEventByExternalId(externalId: Int): Event? {
        val result = _eventsRepository.getByExternalId(externalId)
        if (result.isEmpty()) {
            return null
        }
        return result[0]
    }
}