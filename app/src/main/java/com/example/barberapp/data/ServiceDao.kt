package com.example.barberapp.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServiceDao {

    /**
     * Get all services
     *
     * @return
     */
    @Query("SELECT * FROM services")
    fun getAllServices(): LiveData<List<Service>>

    /**
     * Get service by id
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM services WHERE serviceId = :id")
    fun getServiceById(id: Int): Service?

    /**
     * Insert all
     *
     * @param services
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(services: List<Service>)

    /**
     * Get services by ids
     *
     * @param ids
     * @return
     */
    @Query("SELECT * FROM services WHERE serviceId IN (:ids)")
    fun getServicesByIds(ids: List<Int>): LiveData<List<Service>>

}

