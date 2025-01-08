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

    @Insert
    suspend fun insertAll(vararg ticket: Ticket)

    @Delete
    suspend fun delete(ticket: Ticket)
}