package com.example.barberapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.barberapp.UtilityClasses.AppointmentDetails
import com.example.barberapp.UtilityClasses.AppointmentWithDuration

@Dao
interface AppointmentDao: BaseDao<Appointment> {

    @Query("""
        SELECT * FROM appointments 
        WHERE clientId = :clientId
    """)
    fun getAppointmentsByClient(clientId: Int): LiveData<List<Appointment>>

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

    @Query(
        """
        SELECT 
            a.AppointmentId AS appointmentId,
            a.date AS date,
            a.time AS time,
            a.status AS status,
            b.name AS barberName,
            bs.name AS barbershopName,
            s.name AS serviceName,
            bsrv.price AS price
        FROM appointments AS a
        INNER JOIN barber_services AS bsrv ON a.barberServiceId = bsrv.barberServiceId
        INNER JOIN barbers AS b ON bsrv.barberId = b.barberId
        INNER JOIN barbershops AS bs ON b.barbershopId = bs.barbershopId
        INNER JOIN services AS s ON bsrv.serviceId = s.serviceId
        WHERE a.clientId = :clientId
        """
        )
        fun getAppointmentDetailsForClient(clientId: Int): List<AppointmentDetails>

    @Query(
        """
    SELECT 
        a.AppointmentId AS appointmentId,
        a.date AS date,
        a.time AS time,
        a.status AS status,
        b.name AS barberName,
        bs.name AS barbershopName,
        s.name AS serviceName,
        bsrv.price AS price,
        c.name AS clientName
    FROM appointments AS a
    INNER JOIN barber_services AS bsrv ON a.barberServiceId = bsrv.barberServiceId
    INNER JOIN barbers AS b ON bsrv.barberId = b.barberId
    INNER JOIN barbershops AS bs ON b.barbershopId = bs.barbershopId
    INNER JOIN services AS s ON bsrv.serviceId = s.serviceId
    INNER JOIN clients AS c ON a.clientId = c.clientId
    WHERE b.barberId = :barberId
    """
    )
    fun getAppointmentsForBarber(barberId: Int): List<AppointmentDetails>

    @Query(
        """
    SELECT 
        a.AppointmentId AS appointmentId,
        a.date AS date,
        a.time AS time,
        a.status AS status,
        bs.name AS barbershopName,
        b.name AS barberName,
        s.name AS serviceName,
        bsrv.price AS price,
        c.name AS clientName
    FROM appointments AS a
    INNER JOIN barber_services AS bsrv ON a.barberServiceId = bsrv.barberServiceId
    INNER JOIN barbers AS b ON bsrv.barberId = b.barberId
    INNER JOIN barbershops AS bs ON b.barbershopId = bs.barbershopId
    INNER JOIN services AS s ON bsrv.serviceId = s.serviceId
    LEFT JOIN clients AS c ON a.clientId = c.clientId
    WHERE a.appointmentId = :appointmentId
    """
    )
    fun getAppointmentDetailsById(appointmentId: Int): AppointmentDetails?

    @Query("UPDATE appointments SET status = :status WHERE appointmentId = :appointmentId")
    fun updateAppointmentStatus(appointmentId: Int, status: String)




}
