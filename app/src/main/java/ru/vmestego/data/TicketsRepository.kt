package ru.vmestego.data

import kotlinx.coroutines.flow.Flow

// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#10

interface TicketsRepository {
    fun insert(ticket: Ticket)
    fun getAllTicketsStream(): Flow<Ticket>
}