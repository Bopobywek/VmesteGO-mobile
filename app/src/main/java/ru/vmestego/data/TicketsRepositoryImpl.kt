package ru.vmestego.data

class TicketsRepositoryImpl(private val ticketDao: TicketDao) : TicketsRepository {
    override suspend fun insert(ticket: Ticket): Long {
        return ticketDao.insert(ticket)
    }

    override fun getAllTicketsWithEvents() = ticketDao.getAllWithEvents()

    override fun getAllTicketsStream() = ticketDao.getAll()

    override suspend fun getTicketWithEvent(id: Long): TicketWithEvent {
        return ticketDao.getWithEvent(id)
    }

    override suspend fun deleteTicketById(id: Long) = ticketDao.delete(id)
}