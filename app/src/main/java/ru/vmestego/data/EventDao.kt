package ru.vmestego.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    suspend fun getAll(): List<Event>


    @Query("SELECT * FROM events e WHERE uid = :id")
    suspend fun get(id: Int): List<Event>

    @Query("SELECT * FROM events WHERE title LIKE :pattern || '%'")
    suspend fun search(pattern: String): List<Event>

    @Insert
    suspend fun insert(event: Event): Long

    @Delete
    suspend fun delete(event: Event)
}