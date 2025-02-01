package ru.vmestego.data

// https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#10

interface TicketsRepository {
    suspend fun insert(ticket: Ticket): Long
    suspend fun getAllTicketsWithEvents(): List<TicketWithEvent>
    suspend fun getAllTicketsStream(): List<Ticket>
    suspend fun getTicketWithEvent(id: Long): TicketWithEvent
}