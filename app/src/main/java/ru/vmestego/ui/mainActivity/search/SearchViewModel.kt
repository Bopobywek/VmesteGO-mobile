package ru.vmestego.ui.mainActivity.search

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.datasources.EventsPagingDataSource
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.bll.services.search.SearchService
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.utils.TokenDataProvider
import java.time.LocalDateTime

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenDataProvider = TokenDataProvider(application)

    var searchText by mutableStateOf("")
        private set

    var startDateFilter by mutableStateOf<LocalDateTime?>(null)
        private set

    var endDateFilter by mutableStateOf<LocalDateTime?>(null)
        private set

    var categoriesApplied by mutableStateOf<List<CategoryUi>>(listOf())
        private set

    var visibility by mutableStateOf<Int>(0)
        private set

    private val _categories = MutableStateFlow<List<CategoryUi>>(listOf())
    val categories = _categories.asStateFlow()

    var privateEventsPager = mutableStateOf<Pager<Int, EventUi>?>(null)
        private set

    var joinedPrivateEventsPager = mutableStateOf<Pager<Int, EventUi>?>(null)
        private set

    var publicEventsPager = mutableStateOf<Pager<Int, EventUi>?>(null)
        private set

    var createdPublicEventsPager = mutableStateOf<Pager<Int, EventUi>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val searchService = SearchService()
    private val _eventsService = EventsService()

    init {
        getAllEvents()
        getAllCategories()
    }

    fun changeVisibility() {
        visibility = visibility.plus(1).mod(3)
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
        getAllEvents(searchText)
    }

    fun applyCategoriesFilter(categories: List<CategoryUi>) {
        categoriesApplied = categories
        getAllEvents(searchText)
    }

    fun resetDateFilters() {
        startDateFilter = null
        endDateFilter = null
        getAllEvents(searchText)
    }

    fun resetCategoriesFilter() {
        categoriesApplied = listOf()
        getAllEvents(searchText)
    }

    private fun getAllEvents(query: String? = null) {
        val token = tokenDataProvider.getToken()!!
        val from = startDateFilter?.toLocalDate()
        val to = endDateFilter?.toLocalDate()

        publicEventsPager.value = Pager(
            PagingConfig(pageSize = 10)
        ) {
            EventsPagingDataSource(
                searchService::getOtherAdminsPublicEvents,
                token,
                query,
                categoriesApplied.map { it.id.toString() },
                from,
                to)
        }
        createdPublicEventsPager.value = Pager(
            PagingConfig(pageSize = 10)
        ) {
            EventsPagingDataSource(
                searchService::getPublicEvents,
                token,
                query,
                categoriesApplied.map { it.id.toString() },
                from,
                to)
        }
        joinedPrivateEventsPager.value = Pager(
            PagingConfig(pageSize = 10)
        ) {
            EventsPagingDataSource(
                searchService::getJoinedPrivateEvents,
                token,
                query,
                categoriesApplied.map { it.id.toString() },
                from,
                to)
        }
        privateEventsPager.value = Pager(
            PagingConfig(pageSize = 10)
        ) {
            EventsPagingDataSource(
                searchService::getPrivateEvents,
                token,
                query,
                categoriesApplied.map { it.id.toString() },
                from,
                to)
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
        } else {
            getAllEvents(query)
        }
    }
}

