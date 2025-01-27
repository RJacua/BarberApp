package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BarberScheduleDao: BaseDao<BarberService> {

    /**
     * Get schedules by barber id
     *
     * @param barberId
     * @return
     */
    @Query("SELECT * FROM barber_schedules WHERE barberId = :barberId")
    fun getSchedulesByBarber(barberId: Int): LiveData<List<BarberSchedule>>

    /**
     * Get schedules by day and barber id
     *
     * @param barberId
     * @param dayOfWeek
     * @return
     */
    @Query("""
        SELECT * FROM barber_schedules 
        WHERE barberId = :barberId AND dayOfWeek = :dayOfWeek
    """)
    fun getSchedulesByDay(barberId: Int, dayOfWeek: Int): List<BarberSchedule>

    /**
     * Delete schedules by barber id
     *
     * @param barberId
     */
    @Query("DELETE FROM barber_schedules WHERE barberId = :barberId")
    fun deleteSchedulesByBarber(barberId: Int)

    /**
     * Insert barber schedule
     *
     * @param barberSchedule
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(barberSchedule: BarberSchedule)

    /**
     * Delete schedules by barber id
     *
     * @param barberId
     */
    @Query("DELETE FROM barber_schedules WHERE barberId = :barberId")
    fun deleteSchedulesForBarber(barberId: Int)

    /**
     * Get schedules by barber id
     *
     * @param barberId
     * @return
     */
    @Query("SELECT * FROM barber_schedules WHERE barberId = :barberId")
    fun getSchedulesForBarber(barberId: Int): List<BarberSchedule>

}

