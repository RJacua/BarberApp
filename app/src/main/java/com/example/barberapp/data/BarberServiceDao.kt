package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Time

@Dao
interface BarberServiceDao : BaseDao<BarberService> {

    @Query(
        """
        SELECT * FROM services 
        INNER JOIN barber_services 
        ON services.serviceId = barber_services.serviceId 
        WHERE barber_services.barberId = :barberId
    """
    )
    fun getServicesByBarber(barberId: Int): List<Service>

    @Query(
        """
        SELECT * FROM barbers 
        INNER JOIN barber_services 
        ON barbers.barberId = barber_services.barberId 
        WHERE barber_services.serviceId = :serviceId
    """
    )
    fun getBarbersByService(serviceId: Int): List<Barber>

    @Query("DELETE FROM barber_services WHERE barberId = :barberId")
    fun deleteServicesByBarber(barberId: Int)

    @Query("DELETE FROM barber_services WHERE serviceId = :serviceId")
    fun deleteBarbersByService(serviceId: Int)

    @Query(
        """
        SELECT * FROM barber_services
    """
    )
    fun getAllBarberServices(): List<BarberService>

    @Query(
        """
        SELECT 
            services.serviceId AS serviceId,
            services.name AS name,
            services.description AS description,
            barber_services.price AS price,
            barber_services.duration AS duration
        FROM services
        INNER JOIN barber_services 
        ON services.serviceId = barber_services.serviceId
        WHERE barber_services.barberId = :barberId
    """
    )
    fun getDetailedServicesByBarber(barberId: Int): List<BarberServiceDetail>

    // Método para inserir múltiplos registros com anotação válida do Room
    @Insert
    fun insertAll(barberServices: List<BarberService>)

    @Query(
        """
    SELECT * FROM barber_services
    WHERE barberId = :barberId AND serviceId = :serviceId
    LIMIT 1
    """
    )
    fun getBarberServiceById(barberId: Int, serviceId: Int): BarberService?





}
