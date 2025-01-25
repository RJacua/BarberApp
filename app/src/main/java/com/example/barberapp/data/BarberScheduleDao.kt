package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarberScheduleDao: BaseDao<BarberService> {

    @Query("SELECT * FROM barber_schedules WHERE barberId = :barberId")
    fun getSchedulesByBarber(barberId: Int): List<BarberSchedule>

    @Query("""
        SELECT * FROM barber_schedules 
        WHERE barberId = :barberId AND dayOfWeek = :dayOfWeek
    """)
    fun getSchedulesByDay(barberId: Int, dayOfWeek: Int): List<BarberSchedule>

    @Query("DELETE FROM barber_schedules WHERE barberId = :barberId")
    fun deleteSchedulesByBarber(barberId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(barberSchedule: BarberSchedule)

    @Query("DELETE FROM barber_schedules WHERE barberId = :barberId")
    fun deleteSchedulesForBarber(barberId: Int)

    @Query("SELECT * FROM barber_schedules WHERE barberId = :barberId")
    fun getSchedulesForBarber(barberId: Int): List<BarberSchedule>

    @Dao
    interface BarberScheduleDao {
        @Query(
            """
        SELECT hours FROM barber_schedules
        WHERE barberId = :barberId 
        AND hours BETWEEN :startTime AND :endTime
        """
        )
        fun getAvailableHours(barberId: Int, startTime: String, endTime: String): LiveData<List<String>>
    }
}

