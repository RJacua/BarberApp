package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AppointmentDao {

    @Query("""
        SELECT * FROM appointments 
        WHERE clientId = :clientId
    """)
    fun getAppointmentsByClient(clientId: Int): List<Appointment>

    @Query("""
        SELECT * FROM appointments 
        WHERE barberId = :barberId
    """)
    fun getAppointmentsByBarber(barberId: Int): List<Appointment>
}
