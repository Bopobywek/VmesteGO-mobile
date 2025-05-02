package ru.vmestego.data

class EventsRepositoryImpl(private val eventDao: EventDao) {
    suspend fun insert(event: Event): Long {
        return eventDao.insert(event)
    }

    suspend fun getAllEvents(): List<Event> = eventDao.getAll()

    suspend fun getByExternalId(id: Int): List<Event> = eventDao.getByExternalId(id)

    suspend fun searchEvents(pattern: String): List<Event> = eventDao.search(pattern)
}