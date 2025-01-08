package ru.vmestego.data

import kotlinx.coroutines.flow.Flow

class TicketsRepositoryImpl(private val ticketDao: TicketDao) : TicketsRepository {
    override fun insert(ticket: Ticket) {
        ticketDao.insertAll(ticket)
    }

    override fun getAllTicketsStream(): Flow<Ticket> = ticketDao.getAll()
}