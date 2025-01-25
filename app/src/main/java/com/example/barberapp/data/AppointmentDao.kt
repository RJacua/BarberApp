package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AppointmentDao: BaseDao<Appointment> {

    @Query("""
        SELECT * FROM appointments 
        WHERE clientId = :clientId
    """)
    fun getAppointmentsByClient(clientId: Int): List<Appointment>

    @Query("""
        SELECT a.* 
        FROM appointments a
        INNER JOIN barber_services bs ON a.barberServiceId = bs.barberServiceId
        WHERE bs.barberId = :barberId
    """)
    fun getAppointmentsByBarber(barberId: Int): List<Appointment>

    @Query("""
    SELECT a.time, bs.duration 
    FROM appointments a
    INNER JOIN barber_services bs ON a.barberServiceId = bs.barberServiceId
    WHERE bs.barberId = :barberId AND a.date = :date
""")
    fun getAppointmentsWithDurationByBarber(barberId: Int, date: String): List<AppointmentWithDuration>

}
