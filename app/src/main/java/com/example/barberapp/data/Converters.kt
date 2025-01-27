package com.example.barberapp.data

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import java.sql.Time

class LocalDateConverter {

    /**
     * From time
     *
     * @param time
     * @return
     */
    @TypeConverter
    fun fromTime(time: Time?): Long? {
        return time?.time
    }

    /**
     * To time
     *
     * @param milliseconds
     * @return
     */
    @TypeConverter
    fun toTime(milliseconds: Long?): Time? {
        return milliseconds?.let { Time(it) }
    }

}