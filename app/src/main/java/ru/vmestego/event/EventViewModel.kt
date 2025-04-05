package ru.vmestego.event

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class EventViewModel(application: Application) : AndroidViewModel(application) {
    var status by mutableIntStateOf(2)
        private set

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    fun changeEventStatus(eventId: Long, statusValue: Int) {
        status = statusValue
        viewModelScope.launch(Dispatchers.IO) {
            val response: HttpResponse = client.post("http://10.0.2.2:8080/events/${eventId}/change-status/${statusValue}") {
                contentType(ContentType.Application.Json)
            }
        }
    }
}