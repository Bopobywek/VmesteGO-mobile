package ru.vmestego.data

class TicketsRepositoryImpl(private val ticketDao: TicketDao) : TicketsRepository {
    override suspend fun insert(ticket: Ticket): Long {
        return ticketDao.insert(ticket)
    }

    override suspend fun getAllTicketsWithEvents(): List<TicketWithEvent> {
        return ticketDao.getAllWithEvents()
    }

    override suspend fun getAllTicketsStream(): List<Ticket> = ticketDao.getAll()
    override suspend fun getTicketWithEvent(id: Long): TicketWithEvent {
        return ticketDao.getWithEvent(id)
    }
}