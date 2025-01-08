package ru.vmestego.data

class TicketsRepositoryImpl(private val ticketDao: TicketDao) : TicketsRepository {
    override suspend fun insert(ticket: Ticket) {
        ticketDao.insertAll(ticket)
    }

    override suspend fun getAllTicketsStream(): List<Ticket> = ticketDao.getAll()
}