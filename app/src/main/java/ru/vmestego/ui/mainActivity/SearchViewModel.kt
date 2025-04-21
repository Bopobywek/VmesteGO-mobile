package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.bll.services.search.SearchService
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.event.EventUi
import ru.vmestego.utils.TokenDataProvider
import java.time.LocalDateTime

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDataProvider = TokenDataProvider(application)
    private val _eventsRepository : EventsRepositoryImpl = EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    var searchText by mutableStateOf("")
        private set

    var startDateFilter by mutableStateOf<LocalDateTime?>(null)
        private set

    var endDateFilter by mutableStateOf<LocalDateTime?>(null)
        private set

    var categoriesApplied by mutableStateOf<List<CategoryUi>>(listOf())
        private set

    private val _events = MutableStateFlow<List<EventUi>>(listOf())
    val events = _events.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryUi>>(listOf())
    val categories = _categories.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private val searchService = SearchService()
    private val _eventsService = EventsService()

    init {
        getAllEvents()
        getAllCategories()
    }

    fun update() {
        onSearch(searchText)
    }

    fun onQueryChanged(newQuery: String) {
        searchText = newQuery
        onSearch(newQuery)
    }

    fun applyDateFilter(startDate: LocalDateTime, endDate: LocalDateTime?) {
        startDateFilter = startDate
        endDateFilter = endDate
        viewModelScope.launch(Dispatchers.Default) {
            val oldEvents = _events.value

            val newEvents = oldEvents.filter {
                it.dateTime >= startDate && (endDate == null || it.dateTime < endDate)
            }

            _events.update {
                newEvents
            }
        }
    }

    fun applyCategoriesFilter(categories: List<CategoryUi>) {
        categoriesApplied = categories
        viewModelScope.launch(Dispatchers.Default) {
            val oldEvents = _events.value

            val newEvents = oldEvents.filter {
                it.categories.any { oldEventCat -> categories.any { c -> c.id == oldEventCat.id }}
            }

            _events.update {
                newEvents
            }
        }
    }

    fun resetDateFilters() {
        startDateFilter = null
        endDateFilter = null
        getAllEvents()
    }

    fun resetCategoriesFilter() {
        categoriesApplied = listOf()
        getAllEvents()
    }

    private fun getAllEvents() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {

            var response = searchService.getPrivateEvents(token)
            _events.update {
                response.map {
                    it.toEventUi()
                }
            }

            response = searchService.getPublicEvents(token)
            _events.update {
                val newVals = response.map {
                    it.toEventUi()
                }
                val updated = _events.value.toMutableList() + newVals
                updated
            }

            response = searchService.getJoinedPrivateEvents(token)
            _events.update {
                val newVals = response.map {
                    it.toEventUi()
                }
                val updated = _events.value.toMutableList() + newVals
                updated
            }

            response = searchService.getOtherAdminsPublicEvents(token)
            _events.update {
                val newVals = response.map {
                    it.toEventUi()
                }
                val updated = _events.value.toMutableList() + newVals
                updated
            }
        }
    }

    private fun getAllCategories() {
        val token = tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val response = _eventsService.getAllAvailableCategories(token)
            _categories.update {
                response.map {
                    it.toCategoryUi()
                }
            }
        }
    }

    fun onSearch(query: String) {
        if (query.isEmpty()) {
            getAllEvents()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
//            val localEvents = _eventsRepository.getAllEvents().filter {
//                it.title.startsWith(query)
//            }
//
//            _events.clear()
//            localEvents.forEach {
//                _events.apply {
//                    add(EventUi(it.uid.toLong(), it.title, it.location, it.startAt.toLocalDate(), ""))
//                }
//            }
//            withContext(Dispatchers.Main) {
//                isLoading = true
//            }
//
//            val responseData = searchService.searchEvents(query)
//
//            responseData.events.forEach {
//                val date: LocalDate =
//                Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
//                _events.apply {
//                    add(EventUi(it.id, it.title, it.location, date, it.description))
//                }
//            }
//
//            withContext(Dispatchers.Main) {
//                isLoading = false
//            }
        }
    }
}

