package ru.vmestego.data

import androidx.room.TypeConverter
import java.time.LocalDate

// https://developer.android.com/training/data-storage/room/referencing-data
class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String {
        return date.toString()
    }
}