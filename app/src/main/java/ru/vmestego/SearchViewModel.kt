package ru.vmestego

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.vmestego.data.AppDatabase
import ru.vmestego.data.EventsRepositoryImpl
import ru.vmestego.event.EventUi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _eventsRepository : EventsRepositoryImpl = EventsRepositoryImpl(AppDatabase.getDatabase(application).eventDao())

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    var searchText by mutableStateOf("")
        private set

    private val _events = mutableStateListOf<EventUi>()
    val events: List<EventUi> = _events

    var isLoading by mutableStateOf(false)
        private set

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
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/events") {
                contentType(ContentType.Application.Json)
            }

            val responseData = response.body<SearchEventsResponse>()

            _events.clear()
            responseData.events.forEach {
                val date: LocalDate =
                    Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                _events.apply {
                    add(EventUi(it.id, it.title, it.location, date, it.description))
                }
            }

            val localEvents = _eventsRepository.getAllEvents()

            localEvents.forEach {
                _events.apply {
                    add(EventUi(it.uid.toLong() + 1500, it.title, it.location, it.startAt.toLocalDate(), ""))
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

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/search") {
                url {
                    parameters["q"] = query
                }

                contentType(ContentType.Application.Json)
            }

            val responseData = response.body<SearchEventsResponse>()

            _events.clear()
            responseData.events.forEach {
                val date: LocalDate =
                Instant.ofEpochSecond(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                _events.apply {
                    add(EventUi(it.id, it.title, it.location, date, it.description))
                }
            }

            val localEvents = _eventsRepository.getAllEvents().filter {
                it.title.startsWith(query)
            }

            localEvents.forEach {
                _events.apply {
                    add(EventUi(it.uid.toLong() + 1500, it.title, it.location, it.startAt.toLocalDate(), ""))
                }
            }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }
}

@Serializable
data class SearchEventsResponse(
    val events: List<EventResponse>
)

@Serializable
data class EventResponse(
    val id: Long,
    val title: String,
    val location: String,
    val date: Long,
    val description: String = "",
    val image: String = ""
)