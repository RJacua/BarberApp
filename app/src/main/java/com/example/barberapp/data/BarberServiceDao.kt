package com.example.barberapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.barberapp.UtilityClasses.BarberServiceDetail

@Dao
interface BarberServiceDao : BaseDao<BarberService> {

    /**
     * Get services by barber id
     *
     * @param barberId
     * @return
     */
    @Query(
        """
        SELECT * FROM services 
        INNER JOIN barber_services 
        ON services.serviceId = barber_services.serviceId 
        WHERE barber_services.barberId = :barberId
    """
    )
    fun getServicesByBarber(barberId: Int): List<Service>

    /**
     * Get barbers by service id
     *
     * @param serviceId
     * @return
     */
    @Query(
        """
        SELECT * FROM barbers 
        INNER JOIN barber_services 
        ON barbers.barberId = barber_services.barberId 
        WHERE barber_services.serviceId = :serviceId
    """
    )
    fun getBarbersByService(serviceId: Int): List<Barber>

    /**
     * Delete services by barber id
     *
     * @param barberId
     */
    @Query("DELETE FROM barber_services WHERE barberId = :barberId")
    fun deleteServicesByBarber(barberId: Int)

    /**
     * Delete barbers by service id
     *
     * @param serviceId
     */
    @Query("DELETE FROM barber_services WHERE serviceId = :serviceId")
    fun deleteBarbersByService(serviceId: Int)

    /**
     * Get all barber services
     *
     * @return
     */
    @Query(
        """
        SELECT * FROM barber_services
    """
    )
    fun getAllBarberServices(): List<BarberService>

    /**
     * Get detailed services by barber id
     *
     * @param barberId
     * @return
     */
    @Query(
        """
        SELECT 
            services.serviceId AS serviceId,
            services.name AS name,
            services.description AS description,
            barber_services.price AS price,
            barber_services.duration AS duration,
            barber_services.isActive as isActive
        FROM services
        INNER JOIN barber_services 
        ON services.serviceId = barber_services.serviceId
        WHERE barber_services.barberId = :barberId
    """
    )
    fun getDetailedServicesByBarber(barberId: Int): List<BarberServiceDetail>

    /**
     * Insert all
     *
     * @param barberServices
     */
    @Insert
    fun insertAll(barberServices: List<BarberService>)

    /**
     * Get barber service by id
     *
     * @param barberId
     * @param serviceId
     * @return
     */
    @Query(
        """
    SELECT * FROM barber_services
    WHERE barberId = :barberId AND serviceId = :serviceId
    LIMIT 1
    """
    )
    fun getBarberServiceById(barberId: Int, serviceId: Int): BarberService?

}
