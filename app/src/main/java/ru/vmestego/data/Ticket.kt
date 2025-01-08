package ru.vmestego.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "event_name") val eventName: String,
    @ColumnInfo(name = "location_name") val locationName: String,
    @ColumnInfo(name = "event_date") val eventDate: LocalDate,
    @ColumnInfo(name = "uri") val uri: String
)
