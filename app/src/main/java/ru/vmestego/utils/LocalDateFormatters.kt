package ru.vmestego.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LocalDateFormatters {
    companion object {
        fun formatByDefault(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("EE, dd MMM yyyy", Locale("ru")) // Russian locale
            return date.format(formatter)
        }

        fun parseByDefault(date: String): LocalDate {
            return LocalDate.parse(date)
        }
    }
}

class LocalDateTimeFormatters {
    companion object {
        fun formatByDefault(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern("EE, dd MMM yyyy HH:mm", Locale("ru")) // Russian locale
            return dateTime.format(formatter)
        }

        fun parseByDefault(dateTime: String): LocalDateTime {
            return LocalDateTime.parse(dateTime)
        }
    }
}