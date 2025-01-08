package ru.vmestego.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Ticket::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
//                context.deleteDatabase("item_database")
                Room.databaseBuilder(context, AppDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}