package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface BarberServiceDao {

    @Query("""
        SELECT * FROM services 
        INNER JOIN barber_services 
        ON services.serviceId = barber_services.serviceId 
        WHERE barber_services.barberId = :barberId
    """)
    fun getServicesByBarber(barberId: Int): List<Service>

    @Query("""
        SELECT * FROM barbers 
        INNER JOIN barber_services 
        ON barbers.barberId = barber_services.barberId 
        WHERE barber_services.serviceId = :serviceId
    """)
    fun getBarbersByService(serviceId: Int): List<Barber>

    @Delete
    fun delete(barberService: BarberService)

    @Query("DELETE FROM barber_services WHERE barberId = :barberId")
    fun deleteServicesByBarber(barberId: Int)

    @Query("DELETE FROM barber_services WHERE serviceId = :serviceId")
    fun deleteBarbersByService(serviceId: Int)
}