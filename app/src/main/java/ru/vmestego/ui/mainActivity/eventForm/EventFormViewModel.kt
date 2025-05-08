package ru.vmestego.ui.mainActivity.eventForm

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.events.models.CreateEventRequest
import ru.vmestego.bll.services.events.EventsService
import ru.vmestego.bll.services.shared.models.EventResponse
import ru.vmestego.ui.mainActivity.CategoryUi
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.mainActivity.toCategoryUi
import ru.vmestego.ui.mainActivity.toEventUi
import ru.vmestego.utils.TokenDataProvider
import java.io.InvalidObjectException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventFormViewModel(application: Application) : AndroidViewModel(application) {
    private val _categories = MutableStateFlow<List<CategoryUi>>(listOf())
    val categories = _categories.asStateFlow()

    var existingEventId by mutableStateOf<Long?>(null)
        private set
    var date = mutableStateOf(LocalDate.now())
    var time = mutableStateOf(LocalTime.now())
    var title = mutableStateOf("")
    var location = mutableStateOf("")
    var description = mutableStateOf("")
    var ageRestriction = mutableStateOf("")
    var price = mutableStateOf("")
    var selectedCategories = mutableStateOf(setOf<CategoryUi>())
    var isPrivate = mutableStateOf(true)

    var titleError = mutableStateOf<String?>(null)
    var locationError = mutableStateOf<String?>(null)
    var descriptionError = mutableStateOf<String?>(null)

    private val _tokenDataProvider = TokenDataProvider(application)
    private val _eventsService = EventsService()

    init {
        getAllCategories()
    }

    suspend fun loadExistingForEdit(eventId: Long) {
        val token = _tokenDataProvider.getToken()!!

        existingEventId = eventId
        val event = _eventsService.getEventById(token, eventId)
        // TODO handle null
        if (event == null) {
            return
        }

        title.value = event.title
        location.value = event.location
        description.value = event.description
        date.value = event.dates.toLocalDate()
        time.value = event.dates.toLocalTime()
        selectedCategories.value = event.categories.map { it.toCategoryUi() }.toSet()
    }

    fun isAdmin() : Boolean {
        return _tokenDataProvider.isAdmin()
    }

    private fun validateForm(): Boolean {
        var isValid = true

        titleError.value = if (title.value.isBlank()) {
            isValid = false
            "Название события должно быть заполнено"
        } else null

        locationError.value = if (location.value.isBlank()) {
            isValid = false
            "Место проведения должно быть заполнено"
        } else null

        descriptionError.value = if (description.value.isBlank()) {
            isValid = false
            "Описание должно быть заполнено"
        } else null

        return isValid
    }

    fun updateTitle(newValue: String) {
        title.value = newValue
        titleError.value = null
    }

    fun updateLocation(newValue: String) {
        location.value = newValue
        locationError.value = null
    }

    fun updateDescription(newValue: String) {
        description.value = newValue
        descriptionError.value = null
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

    suspend fun createEvent(): BlResult<EventUi> {
        if (!validateForm()) {
            return BlResult(null, ErrorType.FORM_VALIDATION)
        }

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

        val response = if (existingEventId != null) {
            _eventsService.updateEvent(token, existingEventId!!, request)
        } else {
            _eventsService.createEvent(token, request)
        }

        if (response != null) {
            return return BlResult(response.toEventUi(), null)
        }
        return BlResult(null, ErrorType.API_ERROR)
    }
}

class BlResult<T>(
    private val result: T?,
    val error: ErrorType?
) {
    fun isError(): Boolean {
        return error != null
    }

    fun isSuccess(): Boolean {
        return error == null
    }

    fun getResultIfNotNull(): T {
        if (result == null) {
            throw InvalidObjectException("Result is null")
        }

        return result
    }
}

enum class ErrorType {
    FORM_VALIDATION,
    API_ERROR
}