package ru.vmestego.data

import kotlinx.coroutines.flow.Flow

// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#10

interface TicketsRepository {
    suspend fun insert(ticket: Ticket): Long
    fun getAllTicketsWithEvents(): Flow<List<TicketWithEvent>>
    fun getAllTicketsStream(): Flow<List<Ticket>>
    suspend fun getTicketWithEvent(id: Long): TicketWithEvent
    suspend fun deleteTicketById(id: Long)
}