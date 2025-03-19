package ru.vmestego.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "external_id") val externalId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "start_at") val startAt: LocalDateTime,
    @ColumnInfo(name = "is_synchronized") val isSynchronized: Boolean,
)