package ru.vmestego.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// https://stackoverflow.com/a/75551419
// https://developer.android.com/training/data-storage/room
@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets")
    fun getAll(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets")
    fun getAllWithEvents(): Flow<List<TicketWithEvent>>

    @Query("SELECT * FROM tickets WHERE uid = :id")
    suspend fun getWithEvent(id: Long): TicketWithEvent

    @Insert
    suspend fun insertAll(vararg ticket: Ticket)

    @Insert
    suspend fun insert(ticket: Ticket): Long

    @Query("DELETE FROM tickets WHERE uid = :uid")
    suspend fun delete(uid: Long)
}