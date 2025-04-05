package ru.vmestego

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.serialization.json.Json
import ru.vmestego.bll.services.search.models.SearchEventsResponse
import ru.vmestego.data.SecureStorage
import ru.vmestego.event.EventUi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val secureStorage = SecureStorage.getStorageInstance(application)
    private val applicationContext = application

    private val _events = mutableStateListOf<EventUi>()
    val events: List<EventUi> = _events

    fun logout() {
        secureStorage.removeToken()
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    init {
        getAllEvents()
    }

    private fun getAllEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.get("http://10.0.2.2:8080/events/want") {
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
        }
    }

}