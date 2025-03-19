package ru.vmestego.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Ticket::class, Event::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
//                context.deleteDatabase("vmestego")
                Room.databaseBuilder(context, AppDatabase::class.java, "vmestego")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}