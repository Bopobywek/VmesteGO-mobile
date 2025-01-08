package ru.vmestego.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// https://developer.android.com/training/data-storage/room
@Dao
interface TicketDao {
    @Query("SELECT * FROM ticket")
    fun getAll(): Flow<Ticket>

    @Insert
    fun insertAll(vararg ticket: Ticket)

    @Delete
    fun delete(ticket: Ticket)
}