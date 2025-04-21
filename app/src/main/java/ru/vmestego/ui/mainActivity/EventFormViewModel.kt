package ru.vmestego.ui.mainActivity

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.events.CreateEventRequest
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.event.EventUi
import ru.vmestego.utils.TokenDataProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventFormViewModel(application: Application) : AndroidViewModel(application) {
    private val _categories = MutableStateFlow<List<CategoryUi>>(listOf())
    val categories = _categories.asStateFlow()

    var date = mutableStateOf(LocalDate.now())
    var time = mutableStateOf(LocalTime.now())
    var title = mutableStateOf("")
    var location = mutableStateOf("")
    var description = mutableStateOf("")
    var ageRestriction = mutableStateOf("")
    var price = mutableStateOf("")
    var selectedCategories = mutableStateOf(setOf<CategoryUi>())
    var isPrivate = mutableStateOf(true)

    private val _tokenDataProvider = TokenDataProvider(application)
    private val _eventsService = EventsService()

    init {
        getAllCategories()
    }

    private fun getAllCategories() {
        val token = _tokenDataProvider.getToken()!!

        viewModelScope.launch(Dispatchers.IO) {
            val response = _eventsService.getAllAvailableCategories(token)
            _categories.update {
                response.map {
                    it.toCategoryUi()
                }
            }
        }
    }

    suspend fun createEvent(): EventUi? {
        val token = _tokenDataProvider.getToken()!!

        val request = CreateEventRequest(
            title = title.value,
            dates = LocalDateTime.of(date.value, time.value),
            location = location.value,
            description = description.value,
            ageRestriction = ageRestriction.value.toIntOrNull() ?: 0,
            price = price.value.toDoubleOrNull() ?: 0.0,
            isPrivate = isPrivate.value,
            eventCategoryNames = selectedCategories.value.map { it.name },
            eventImages = emptyList(),
            externalId = null
        )

        val response = _eventsService.createEvent(token, request)
        if (response != null) {
            return response.toEventUi()
        }
        return null
    }
}