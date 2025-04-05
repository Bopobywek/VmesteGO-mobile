package ru.vmestego.bll.services.search.models

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val id: Long,
    val title: String,
    val location: String,
    val date: Long,
    val description: String = "",
    val image: String = ""
)