package com.example.barberapp.data

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import java.sql.Time

class LocalDateConverter {
    @TypeConverter
    fun toLocalDate(value: String): LocalDateTime {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime): String {
        return date.toString()
    }

    // Converter de Time para Long (milissegundos)
    @TypeConverter
    fun fromTime(time: Time?): Long? {
        return time?.time // Transforma Time em milissegundos
    }

    // Converter de Long (milissegundos) para Time
    @TypeConverter
    fun toTime(milliseconds: Long?): Time? {
        return milliseconds?.let { Time(it) }
    }

}