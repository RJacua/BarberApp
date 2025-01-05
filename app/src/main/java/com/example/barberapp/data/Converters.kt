package com.example.barberapp.data

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

class LocalDateConverter {
    @TypeConverter
    fun toLocalDate(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime): String {
        return date.toString()
    }

}