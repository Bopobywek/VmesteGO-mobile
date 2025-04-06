package ru.vmestego

import android.app.Application
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vmestego.bll.services.search.SearchService
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.event.EventUi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _eventsRepository : EventsRepositoryImpl = EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    var searchText by mutableStateOf("")
        private set

    private val _events = mutableStateListOf<EventUi>()
    val events: List<EventUi> = _events

    var isLoading by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private val searchService = SearchService()

    init {
        getAllEvents()
    }

    fun update() {
        onSearch(searchText)
    }

    fun onQueryChanged(newQuery: String) {
        searchText = newQuery
        onSearch(newQuery)
    }

    private fun getAllEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val localEvents = _eventsRepository.getAllEvents()

            _events.clear()
            localEvents.forEach {
                _events.apply {
                    add(EventUi(it.uid.toLong(), it.title, it.location, it.startAt.toLocalDate(), ""))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = true
            }

            val responseData = searchService.getAllEvents()

            responseData.events.forEach {
                val date: LocalDate =
                    Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                _events.apply {
                    add(EventUi(it.id, it.title, it.location, date, it.description))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    fun onSearch(query: String) {
        if (query.isEmpty()) {
            getAllEvents()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val localEvents = _eventsRepository.getAllEvents().filter {
                it.title.startsWith(query)
            }

            _events.clear()
            localEvents.forEach {
                _events.apply {
                    add(EventUi(it.uid.toLong(), it.title, it.location, it.startAt.toLocalDate(), ""))
                }
            }
            withContext(Dispatchers.Main) {
                isLoading = true
            }

            val responseData = searchService.searchEvents(query)

            responseData.events.forEach {
                val date: LocalDate =
                Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                _events.apply {
                    add(EventUi(it.id, it.title, it.location, date, it.description))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }
}

