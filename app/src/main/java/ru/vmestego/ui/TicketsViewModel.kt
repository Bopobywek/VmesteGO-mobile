package ru.vmestego.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.Ticket
import ru.vmestego.data.TicketsRepository
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject

// https://stackoverflow.com/a/71147730
// https://stackoverflow.com/a/67252955
@HiltViewModel
class TicketsViewModel @Inject constructor(private val ticketsRepository: TicketsRepository) : ViewModel() {
    private val _tickets = mutableStateListOf<Ticket>()
    val tickets: List<Ticket> = _tickets

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ticketsRepository.getAllTicketsStream().collect {
                    value -> run {
                Log.i("TicketsViewModelInit", "Collected $value")
                if (value == null) {
                    return@run
                }
                _tickets.apply {
                    val minDay = LocalDate.of(1970, 1, 1).toEpochDay()
                    val maxDay = LocalDate.of(2015, 12, 31).toEpochDay()
                    val randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay)
                    val randomDate = LocalDate.ofEpochDay(randomDay).withYear(2025)
                    add(Ticket("1", randomDate, Uri.parse(value.uri)))
                }
            }
            }
        }
    }

    // https://code.luasoftware.com/tutorials/android/android-access-context-in-viewmodel
    fun addTicket(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            ticketsRepository.getAllTicketsStream().collect {
                    value -> Log.i("TicketsViewModel", "Collected $value")
            }
        }
        val minDay = LocalDate.of(1970, 1, 1).toEpochDay()
        val maxDay = LocalDate.of(2015, 12, 31).toEpochDay()
        val randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay)
        val randomDate = LocalDate.ofEpochDay(randomDay).withYear(2025)
        viewModelScope.launch(Dispatchers.IO) {
            ticketsRepository.insert(ru.vmestego.data.Ticket(eventName = "abc", locationName = "avc", uri = uri.toString()))
        }
        _tickets.apply {
            add(Ticket("1", randomDate, uri))
        }
    }

}