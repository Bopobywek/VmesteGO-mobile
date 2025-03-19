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
    }
}