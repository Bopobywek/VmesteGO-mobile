package ru.vmestego.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// https://developer.android.com/training/data-storage/room/referencing-data
class Converters {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String {
        return date.toString()
    }

    @TypeConverter
    fun fromTimestampDateTime(value: String?): LocalDateTime? {
        return LocalDateTime.parse(value, formatter)
    }

    @TypeConverter
    fun dateTimeToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}