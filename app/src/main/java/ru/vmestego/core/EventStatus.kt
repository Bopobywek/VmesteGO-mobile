package ru.vmestego.core

import kotlinx.serialization.Serializable

@Serializable
enum class EventStatus {
    NotGoing,
    Going,
    WantToGo
}