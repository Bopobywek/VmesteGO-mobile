package ru.vmestego.bll.services.search.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchEventsResponse(
    val events: List<EventResponse>
)