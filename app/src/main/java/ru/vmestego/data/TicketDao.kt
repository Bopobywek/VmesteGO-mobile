package ru.vmestego.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

// https://developer.android.com/training/data-storage/room
@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets")
    suspend fun getAll(): List<Ticket>

    @Query("SELECT * FROM tickets")
    suspend fun getAllWithEvents(): List<TicketWithEvent>

    @Query("SELECT * FROM tickets WHERE uid = :id")
    suspend fun getWithEvent(id: Long): TicketWithEvent

    @Insert
    suspend fun insertAll(vararg ticket: Ticket)

    @Insert
    suspend fun insert(ticket: Ticket): Long

    @Delete
    suspend fun delete(ticket: Ticket)
}