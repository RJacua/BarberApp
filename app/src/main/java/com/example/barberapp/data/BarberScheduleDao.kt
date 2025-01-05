package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarberScheduleDao {

    @Query("SELECT * FROM barber_schedules WHERE barberId = :barberId")
    fun getSchedulesByBarber(barberId: Int): List<BarberSchedule>

    @Query("""
        SELECT * FROM barber_schedules 
        WHERE barberId = :barberId AND dayOfWeek = :dayOfWeek
    """)
    fun getSchedulesByDay(barberId: Int, dayOfWeek: Int): List<BarberSchedule>

    @Delete
    fun delete(schedule: BarberSchedule)

    @Query("DELETE FROM barber_schedules WHERE barberId = :barberId")
    fun deleteSchedulesByBarber(barberId: Int)
}